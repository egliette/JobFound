package com.example.miniproject;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapDirectionHelper {
    private GoogleMap mMap;
    private Context mContext;
    private Marker mkStart, mkDest;
    private Polyline route;

    //https://docs.mapbox.com/api/navigation/directions/
    private final String API_URL_TEMP = "https://api.mapbox.com/directions/v5/mapbox/driving/%s,%s;%s,%s?" +
            "annotations=maxspeed&overview=full&geometries=geojson&access_token=%s";

    public MapDirectionHelper(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        this.mContext = context;
    }

    public void startDirection(LatLng start, LatLng dest) {
        clearDirectionResult();
        requestDirection(start, dest);
    }

    private void drawRoute(ArrayList<LatLng> points) {
        if (points.size() < 1) return;

        mkDest = mMap.addMarker(new MarkerOptions()
                .position(points.get(points.size()-1))
        );

        route = mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.RED)
        );
    }

    public void clearDirectionResult() {
        if (route != null) {
            route.remove();
            route = null;
        }
    }

    @SuppressLint("DefaultLocale")
    private void requestDirection(LatLng start, LatLng dest) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = String.format(API_URL_TEMP,
                start.longitude, start.latitude,
                dest.longitude, dest.latitude,
                mContext.getString(R.string.mapbox_token));
        Log.d("@@@ url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("@@@ response", response);
                        ArrayList<LatLng> points = parseJsonResult(start, dest, response);
                        drawRoute(points);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("@@@ error", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private ArrayList<LatLng> parseJsonResult(LatLng start, LatLng dest, String response)  {
        ArrayList<LatLng> points = new ArrayList<>();

        try {
            JSONObject jsonResult = new JSONObject(response);
            JSONArray jsonPoints = jsonResult.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");

            for (int i = 0; i < jsonPoints.length(); i++) {
                JSONArray tmp = jsonPoints.getJSONArray(i);
                double lat = tmp.getDouble(1);
                double lng = tmp.getDouble(0);
                points.add(new LatLng(lat, lng));
            }

            points.add(0, start);
            points.add(dest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return points;
    }
}

