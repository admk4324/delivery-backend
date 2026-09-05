package com.example.globaldeliveryapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    // استبدل هذا الرابط برابط Vercel الخاص بك الذي قمت بإنشائه
    private static final String BASE_URL = "https://delivery-backend-xyz.vercel.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProductsFromGlobalServer();
    }

    private void loadProductsFromGlobalServer() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET, BASE_URL + "/products", null,
                response -> {
                    productList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            productList.add(new Product(
                                    obj.getString("id"),
                                    obj.getString("name"),
                                    obj.getDouble("price")
                            ));
                        }
                        adapter = new ProductAdapter(productList, MainActivity.this, this::sendOrderToServer);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "تعذر الاتصال بالسيرفر السحابي", Toast.LENGTH_SHORT).show()
        );

        queue.add(arrayRequest);
    }

    private void sendOrderToServer(Product product) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject orderBody = new JSONObject();

        try {
            orderBody.put("productId", product.getId());
            orderBody.put("quantity", 1);
            orderBody.put("userLocation", "موقع تجريبي - الخرطوم");
            orderBody.put("userPhone", "+249123456789");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST, BASE_URL + "/orders", orderBody,
                response -> {
                    try {
                        String msg = response.getString("message");
                        String orderId = response.getString("orderId");
                        Toast.makeText(MainActivity.this, msg + " (" + orderId + ")", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "فشل إرسال الطلب", Toast.LENGTH_SHORT).show()
        );

        queue.add(postRequest);
    }
}