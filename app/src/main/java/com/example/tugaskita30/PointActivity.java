package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PointActivity extends AppCompatActivity {

    Global global = Global.getInstance();

    TextView point_poinact_text;
    CardView paket_rajin, paket_cermat, paket_genius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        point_poinact_text = findViewById(R.id.point_poinact_text);
        paket_rajin = findViewById(R.id.paket_rajin);
        paket_cermat = findViewById(R.id.paket_cermat);
        paket_genius = findViewById(R.id.paket_genius);

        point_poinact_text.setText(global.getDataUserPoint());
        paket_rajin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointActivity.this, TransactionCouponActivity.class);
                intent.putExtra("paket", "Paket Rajin");
                intent.putExtra("subpaket", "Tak pantang menyerah");
                intent.putExtra("point", "3");
                intent.putExtra("harga", "1000");
                intent.putExtra("image", R.drawable.plus_3);
                startActivityForResult(intent,0);
            }
        });
        paket_cermat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointActivity.this, TransactionCouponActivity.class);
                intent.putExtra("paket", "Paket Cermat");
                intent.putExtra("subpaket", "Banyak akal banyak IDE");
                intent.putExtra("point", "5");
                intent.putExtra("harga", "1500");
                intent.putExtra("image", R.drawable.plus_5);
                startActivityForResult(intent,0);
            }
        });
        paket_genius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointActivity.this, TransactionCouponActivity.class);
                intent.putExtra("paket", "Paket Genius");
                intent.putExtra("subpaket", "Rajin dan tekun sekali");
                intent.putExtra("point", "9");
                intent.putExtra("harga", "2000");
                intent.putExtra("image", R.drawable.plus_9);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("intentCallbackTransactionCoupon")) {
                String result = data.getStringExtra("intentCallbackTransactionCoupon");
                point_poinact_text.setText(result); // update point value
            }
        }
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackPoint", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

}

