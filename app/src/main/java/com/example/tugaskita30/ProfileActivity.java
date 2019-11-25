package com.example.tugaskita30;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    Session session;
    Global global = Global.getInstance();
    private String URL_POST_UPDATE = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/update_profile.php");

    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST_CODE = 228;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 4192;
    Uri uriFromGallery = null;

    String CHANNEL_ID = "profile notification";

    TextView processing_prfact_text;
    ImageView profile_prfact_image, qr_prfact_image;
    EditText namalengkap_prfact_input, username_prfact_input, storyofyou_prfact_input;
    Button ubah_profile_prfact_button;
    CheckBox notify_me_check_box;

    String tempNotificactionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);
        tempNotificactionValue = session.getUserNotification();

        createNotificationChannel();

        processing_prfact_text = findViewById(R.id.processing_prfact_text);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newUsername = username_prfact_input.getText().toString().trim();
                final String newStoryOfYou = storyofyou_prfact_input.getText().toString().trim();

                if (uriFromGallery != null) {
                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ProfileActivity.this);
                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(ProfileActivity.this, CHANNEL_ID);
                    builder.setContentTitle("Uploading Image")
                            .setContentText("Starting...")
                            .setSmallIcon(R.drawable.ic_school_blue_24dp)
                            .setColor(ContextCompat.getColor(ProfileActivity.this, R.color.blue))
                            .setSound(Uri.parse("android.resource://" + ProfileActivity.this.getPackageName() + "/" + R.raw.opening))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    // CREATE UNIQUE ID WITH TIME AND DATE
                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss:SSS");
                    final String uniqueID = global.getDataUserId() + "." + ft.format(dNow);

                    Map<String, Object> options = new HashMap<>();
                    options.put("public_id", uniqueID);
                    options.put("tags", "profile");
                    options.put("folder", "tugaskita/profile/");
                    // UPLOAD NEW PROFILE IMAGE
                    String requestId = MediaManager.get().upload(uriFromGallery).callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            builder.setContentText("Cloud is starting for uploading...")
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Cloud is starting for uploading..."))
                                    .setProgress(0,0,true);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            builder.setContentText("Finished")
                                    .setProgress(0,0,false);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            builder.setContentText("Something went wrong while uploading, try again later")
                                    .setProgress(0,0,false);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).options(options).unsigned("qojyihp8").dispatch();
                    update(session.getUserId(), session.getUserNama(), newUsername, newStoryOfYou, uniqueID, tempNotificactionValue);
                } else {
                    update(session.getUserId(), session.getUserNama(), newUsername, newStoryOfYou,"", tempNotificactionValue);
                }
            }
        });

        ubah_profile_prfact_button = findViewById(R.id.ubah_profile_prfact_button);
        ubah_profile_prfact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        namalengkap_prfact_input = findViewById(R.id.namalengkap_prfact_input);
        namalengkap_prfact_input.setText(session.getUserNamaLengkap());

        username_prfact_input = findViewById(R.id.username_prfact_input);
        username_prfact_input.setText(session.getUserNama());

        storyofyou_prfact_input = findViewById(R.id.storyofyou_prfact_input);
        storyofyou_prfact_input.setText(session.getUserCerita());

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        String url = "empty";
        if (isConnected)
            url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(300).crop("fill")).secure(true).generate("tugaskita/profile/".concat(global.getDataUserAvatar()));
        profile_prfact_image = findViewById(R.id.profile_prfact_image);
        Picasso.with(this).load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(global.getDefaultProfilePicture(session.getUserAvatarAlternative()))
                .into(profile_prfact_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        processing_prfact_text.setText("Loaded");
                    }

                    @Override
                    public void onError() {
                        processing_prfact_text.setText("Profile set to default");
                    }
                });

        notify_me_check_box = findViewById(R.id.notifyme_prfact_checkbox);
        if (tempNotificactionValue.equals("1")) {
            notify_me_check_box.setChecked(true);
        } else {
            notify_me_check_box.setChecked(false);
        }
        notify_me_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    tempNotificactionValue = "1";
                else
                    tempNotificactionValue = "0";
            }
        });

        qr_prfact_image = findViewById(R.id.qr_prfact_image);
        String text="add-".concat(session.getUserId()); // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_prfact_image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void openChangePasswordActivity(View view) {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    public void update(final String userid, final String usernama, final String newusername, final String newusercerita, final String newuseravatar, final String newusernotification) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                String upload = jsonObject.getString("upload");
                                // SET UPDATED INFORMATION
                                // AND CHECK IF THERE IS UPLOADING IMAGE
                                if (upload.equals("1")) {
                                    String newProfileImage = newuseravatar.concat(".png");
                                    global.setDataUserAvatar(newProfileImage);
                                }
                                global.setDataUserNama(newusername);
                                global.setDataUserCerita(newusercerita);
                                session.setUserNama(newusername);
                                session.setUserNotification(newusernotification);
                                global.setDataIntentKY("update-profile");
                                Toast.makeText(ProfileActivity.this, "All Saved!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "JSON error: "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            //This indicates that the reuest has time out
                            openErrorDialog("Server terlalu lama merespon! coba lagi nanti");
                        } else if (error instanceof NoConnectionError) {
                            //This indicates that the reuest has no connection
                            openErrorDialog("Check koneksi internet Anda!");
                        } else if (error instanceof AuthFailureError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                            openErrorDialog("Authentication gagal! coba beberapa saat lagi");
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            openErrorDialog("Server Error!");
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            openErrorDialog("Network Anda mengalami error!");
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            openErrorDialog("Server tidak bisa parse permintaan Anda!");
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("usernama", usernama);
                params.put("newusernama", newusername);
                params.put("newusercerita", newusercerita);
                params.put("newusernotification", newusernotification);
                params.put("newuseravatar", newuseravatar);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /**
     * This method will be invoked when the user clicks a button
     */
    public void selectImageFromGallery() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Image Saved.", Toast.LENGTH_LONG).show();
            }
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    uriFromGallery = imageUri;
                    // show the image to the user
                    profile_prfact_image.setImageBitmap(image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackProfile", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    void createNotificationChannel() {
        Uri sound = Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.opening);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = getString(R.string.channel_description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(sound, attributes); // This is IMPORTANT
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
