package com.example.yajya.oh_sorry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Map extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
    static final String API_KEY = "072a8054349bc3dbbb29a2201c04a678";
    net.daum.mf.map.api.MapView mapView;
    LocationManager locationManager;
    HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    double lat, lng;
    int selectedMarker;
    MyDBHandler dbHandler;
    Cursor cursor;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        selectedMarker = -2;

        initDB();
        init();
        loadGPS();
        loadMap();
    }

    public void init(){
        aSwitch = (Switch)findViewById(R.id.showCustom);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    showCustomPlace();
                } else {
                    mapView.removeAllPOIItems();
                }
            }
        });
    }

    public void initDB(){
        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        cursor = dbHandler.getQueryResult("select * from places");
    }

    public void btnList(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnSetting(View view) {
        Intent intent = new Intent(this, SettingActicity.class);
        startActivity(intent);
        finish();
    }

    public void loadGPS() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String formatDate = sdfNow.format(date);
                //Toast.makeText(getApplicationContext(),formatDate+" : "+lat+", "+lng,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void loadMap() {
        mapView = new net.daum.mf.map.api.MapView(this);
        mapView.setDaumMapApiKey(API_KEY);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
    }

    public void goMyLocation(View view) {
        mapView.removeAllPOIItems();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat,lng),true);
        MapPOIItem myMarker = new MapPOIItem();
        myMarker.setItemName("현재 위치");
        myMarker.setTag(-1);
        myMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng));
        myMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        myMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(myMarker);

        selectedMarker = -1;
    }

    public void searchLocation(View view) {
        EditText searchLocationName = (EditText)findViewById(R.id.searchLocationName);

        String locationName = searchLocationName.getText().toString();
        if(locationName == null || locationName.length() == 0){
            Toast.makeText(getApplicationContext(), "검색어를 입력하세요.",Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "검색중...",Toast.LENGTH_SHORT).show();
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchLocationName.getWindowToken(),0);

        int radius = 10000; //  중심 좌표부터의 반경거리 meter단위(0~10000)
        int page = 1;   //  페이지 번호 (1~3). 한페이지에 15개

        Searcher searcher = new Searcher();
        searcher.searchKeyword(getApplicationContext(), locationName, lat, lng, radius, page, API_KEY, new OnFinishSearchListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                mapView.removeAllPOIItems();
                showResult(itemList);
            }

            @Override
            public void onFail() {

            }
        });
    }

    public void btnAdd(View view) {
        int temp = cursor.getCount()+100;
        final int count = temp;

        cursor.moveToFirst();
        if(selectedMarker>=-1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Add?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Item item = mTagItemMap.get(selectedMarker);
                    Place place = new Place(count, item.title, item.latitude, item.longitude);

                    dbHandler.addPlace(place);
                    showCustomPlace();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Search & Select Marker Please", Toast.LENGTH_SHORT).show();
        }
    }

    public void showCustomPlace(){
        mapView.removeAllPOIItems();
        MapPointBounds mapPointBounds = new MapPointBounds();

        cursor = dbHandler.getQueryResult("select * from places");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            MapPOIItem poiItem = new MapPOIItem();
            String str = cursor.getString(1);
            poiItem.setItemName(str);
            int tag = cursor.getInt(0);
            poiItem.setTag(tag);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(cursor.getDouble(2), cursor.getDouble(3));
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            cursor.moveToNext();
        }

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) return null;
            Item item = mTagItemMap.get(poiItem.getTag());
            if (item == null) return null;
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.title);
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            textViewDesc.setText(item.address);
            imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

    }

    private void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }
    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Item item = mTagItemMap.get(mapPOIItem.getTag());
        StringBuilder sb = new StringBuilder();
        if(mapPOIItem.getTag()==-1){
            return;
        } else if(mapPOIItem.getTag()>=100){
            cursor = dbHandler.getQueryResult("select * from places where markertag = "+mapPOIItem.getTag());
            cursor.moveToFirst();
            if(!cursor.isAfterLast())
                sb.append("title=").append(cursor.getString(1)).append("\n");
        }else {
            sb.append("title=").append(item.title).append("\n");
            sb.append("imageUrl=").append(item.imageUrl).append("\n");
            sb.append("address=").append(item.address).append("\n");
            sb.append("newAddress=").append(item.newAddress).append("\n");
            sb.append("zipcode=").append(item.zipcode).append("\n");
            sb.append("phone=").append(item.phone).append("\n");
            sb.append("category=").append(item.category).append("\n");
            sb.append("longitude=").append(item.longitude).append("\n");
            sb.append("latitude=").append(item.latitude).append("\n");
            sb.append("distance=").append(item.distance).append("\n");
            sb.append("direction=").append(item.direction).append("\n");
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        selectedMarker = mapPOIItem.getTag();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
