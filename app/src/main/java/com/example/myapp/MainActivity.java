package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static java.sql.Types.DOUBLE;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;
    Marker myMarker = null;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    static boolean init = false;
    double maps[][];
    ArrayList<LocationInfo> pull_List = new ArrayList<>();
    ArrayList<LatLng> node_List = new ArrayList<>();
    double between_distance[];
    private LatLng dest;
    private LatLng start;
    private LatLng currentLatLng = null;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    //LatLng node1 = new LatLng(37.52487, 126.92723);
    //LatLng node2 = new LatLng(100.02, 9.2);
    //distance = Math.round(computeDistanceBetween(node1, node2)*10)/10; // Km

    //모든 class object들을 LatLan으로 변환하여 다 넣은 리스트 -> node_list[]
    //start_node = index가 0

    // 넣으면 return 값으로 node_nearby_start와 node_nearby_dest사이의 거리를 반환함



    public int get_Node_index_nearby_start_or_dest(LatLng node) {
        int len = pull_List.size();
        int minidx = 0;
        for (int i = 1; i < 5; i++) {
            double myDistance = getDistance(i, node);
            if (myDistance < getDistance(minidx, node)) {
                minidx = i;
            }
        }
        return minidx;
    }


    public double getDistance(int i, LatLng node) {
        double lati = pull_List.get(i).getLatitude();
        double longi = pull_List.get(i).getLongitude();
        double myDistance = computeDistanceBetween(new LatLng(lati, longi), node);
        return myDistance;
    }

    public void pullToNodeList() {
        int len = pull_List.size();
        for (int i = 0; i < 5; i++) {
            double lati = pull_List.get(i).getLatitude();
            double longi = pull_List.get(i).getLongitude();
            node_List.add(new LatLng(lati, longi));
        }
    }

    public void set_maps() {
        pullToNodeList();
        int len = node_List.size();
        maps = new double[len][len];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j <= i; j++) {
                maps[i][j] = computeDistanceBetween(node_List.get(i), node_List.get(j));
                maps[j][i] = computeDistanceBetween(node_List.get(i), node_List.get(j));
            }
        }
    }

    public double dijkstra(int v, int k) {
        int len = node_List.size();
        between_distance = new double[len];
        boolean[] check = new boolean[len];

        set_maps();

        //between_distance initialization
        for (int i = 0; i < 5; i++) {
            between_distance[i] = Double.MAX_VALUE;
        }

        between_distance[v] = 0;
        check[v] = true;

        for(int i = 0; i < 5; i++) {
            if(!check[i]) {
                between_distance[i] = maps[v][i];
            }
        }

        for (int a = 0; a < 5; a++) {

            double min = Double.MAX_VALUE;
            int min_index = -1;

            for (int i = 0; i < check.length; i++) {
                Log.d("check", String.valueOf(check[i]));
            }

            for (int i = 0; i < check.length; i++) {
                Log.d("betweenDistance", String.valueOf(between_distance[i]));
            }

            for (int i = 1; i < 5; i++) {
                if (!check[i] && between_distance[i] != Double.MAX_VALUE) {
                    if (between_distance[i] < min) {
                        min = between_distance[i];
                        min_index = i;
                    }
                }
            }

            if (min_index != -1) {
                check[min_index] = true;
                for (int i = 0; i < 5; i++) {
                    if (!check[i] && maps[min_index][i] != 0) {
                        if (between_distance[i] > between_distance[min_index] + maps[min_index][i]) {
                            between_distance[i] = between_distance[min_index] + maps[min_index][i];
                        }
                    }
                }
            }
        }


        double mydistance = between_distance[k];
        return mydistance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.249.19.251:780/api/locations";
        Log.d("dbProcess", url+"");

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {Log.d("onrespone", response+"");
                    for (int i = 0; i < 5; i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int index = Integer.parseInt(jsonObject.getString("index"));
                        String name = jsonObject.getString("name");
                        String address = jsonObject.getString("address");
                        double latitude = Double.parseDouble(jsonObject.getString("latitude"));
                        double longtitude = Double.parseDouble(jsonObject.getString("longtitude"));
                        LocationInfo loc = new LocationInfo(index, name, address, latitude, longtitude);
                        Log.d("dbprocess", loc+"");
                        pull_List.add(loc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("responseError1", "error1");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseError2", "error2");
            }
        });
        queue.add(jsonArrayRequest);
        Toast myToast = Toast.makeText(this.getApplicationContext(), "목적지를 설정하세요", Toast.LENGTH_SHORT);
        myToast.show();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.layout_main);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mMap = googleMap;
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(final LatLng point) {
                Dialog(point, googleMap);
            }
        });
        set_maps();
        // for loop를 통한 n개의 마커 생성
        for (int idx = 0; idx < 5; idx++) {
            // 1. 마커 옵션 설정 (만드는 과정)
            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                    .position(node_List.get(idx))
                    .title("마커" + idx); // 타이틀.

            // 2. 마커 생성 (마커를 나타냄)
            mMap.addMarker(makerOptions);
        }

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());
                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());
                Log.d(TAG, "onLocationResult : " + markerSnippet);
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                init = true;
                mCurrentLocatiion = location;
            }

        }
    };
    public void Dialog(final LatLng point, final GoogleMap googleMap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목적지로 설정하시겠습니까?");
        builder.setMessage("진짜루?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        googleMap.clear();

                        for (int idx = 0; idx < 5; idx++) {
                            // 1. 마커 옵션 설정 (만드는 과정)
                            MarkerOptions makerOptions = new MarkerOptions();
                            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                                    .position(node_List.get(idx))
                                    .title("마커" + idx); // 타이틀.

                            // 2. 마커 생성 (마커를 나타냄)
                            mMap.addMarker(makerOptions);
                        }


                        if (myMarker != null) {myMarker.remove();}

                        MarkerOptions mOptions = new MarkerOptions();
                        // 마커 타이틀
                        mOptions.title("목적지");
                        Double latitude = point.latitude; // 위도
                        Double longitude = point.longitude; // 경도
                        // 마커의 스니펫(간단한 텍스트) 설정
                        mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                        // LatLng: 위도 경도 쌍을 나타냄
                        mOptions.position(new LatLng(latitude, longitude));
                        dest = new LatLng(latitude, longitude);
                        start = currentLatLng;
                        // 마커(핀) 추가
                        myMarker = googleMap.addMarker(mOptions);




                        int nnsidx = get_Node_index_nearby_start_or_dest(start);
                        LatLng nns = node_List.get(nnsidx);
                        int nndidx = get_Node_index_nearby_start_or_dest(dest);
                        LatLng nnd = node_List.get(nndidx);

                        googleMap.addPolyline(new PolylineOptions().add(start, nns).width(25).color(Color.RED));
                        googleMap.addPolyline(new PolylineOptions().add(nnd, nns).width(25).color(Color.BLUE));
                        googleMap.addPolyline(new PolylineOptions().add(dest, nnd).width(25).color(Color.BLACK));


                        출처: https://taetanee.tistory.com/entry/안드로이드-구글맵-선-그리기 [좋은 정보]

                        Toast.makeText(getApplicationContext(), "목적지가 설정되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "목적지를 설정하세요.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");


            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("####", String.valueOf(location.getLatitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);
        if (!init) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation() {

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


}



/*        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey("l7xx4b6b1d4d0842475881de1bdf04d6dac9");
        linearLayoutTmap.addView( tMapView );*/
// tMapView.setCenterPoint(127.365643, 36.374104);

/*
        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        TMapPoint tMapPoint1 = new TMapPoint(37.570841, 126.985302); // SKT타워
// 마커 아이콘

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);

       // markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName("SKT타워"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

        tMapView.setCenterPoint( 126.985302, 37.570841 );*/


