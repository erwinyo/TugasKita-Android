package com.example.tugaskita30;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MyQrCodeActivity extends AppCompatActivity {

    Session session;
    Global global = Global.getInstance();

    ImageView qr_myqrcode_image;
    TextView id_myqrcode_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_code);

        session = new Session(this);

        qr_myqrcode_image = findViewById(R.id.qr_myqrcode_image);
        String text="add-".concat(session.getUserId()); // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_myqrcode_image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        id_myqrcode_text = findViewById(R.id.id_myqrcode_text);
        id_myqrcode_text.setText("ID: ".concat(session.getUserNama()));
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    public void openScanActivity(View view) {
        finish();
    }
}
