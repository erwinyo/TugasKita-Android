package com.example.tugaskita30.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.example.tugaskita30.DashboardActivity;
import com.example.tugaskita30.DialogError;
import com.example.tugaskita30.ForgotPasswordActivity;
import com.example.tugaskita30.Global;
import com.example.tugaskita30.MyService;
import com.example.tugaskita30.R;
import com.example.tugaskita30.Session;
import com.example.tugaskita30.SignupActivity;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    Session session;
    String URL_LOGIN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/login.php");
    String DEVICE_NAME;
    Button loginButton;
    EditText view;
    TextView login;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);
        DeviceName.with(this).request(new DeviceName.Callback() {
            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String name = info.marketName;            // "Galaxy S8+"
                String model = info.model;                // "SM-G955W"
                String codename = info.codename;          // "dream2qltecan"
                String deviceName = info.getName();       // "Galaxy S8+"
                // FYI: We are on the UI thread.
                DEVICE_NAME = manufacturer + " | " + name;
            }
        });

        view = findViewById(R.id.buat_nama_grup_input);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.buat_nama_grup_input);
        final EditText passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        login = findViewById(R.id.login_logact_text);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                // Successful
                login.setText("Hang on...");
                loginButton.setText("Loading...");
                String email = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                login(email, password);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("processing...");
                loginButton.setText("Loading...");
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, MyService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000) // 15 minutes
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            //Toast.makeText(SplashActivity.this, "Job scheduling started", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(SplashActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        //String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        login.setText("Almost Done...");
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        login.setText("Something Wrong!");
        loginButton.setText("Start Again");
    }

    public void openForgotPasswordActivity(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    public void login(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String user_id = object.getString("user_id");
                                    String user_namalengkap = object.getString("user_namalengkap");
                                    String user_nama = object.getString("user_nama");
                                    String user_email = object.getString("user_email");
                                    String user_cerita = object.getString("user_cerita");
                                    String user_avatar = object.getString("user_avatar");
                                    String user_avatar_alternative = object.getString("user_avatar_alternative");
                                    String user_point = object.getString("user_point");
                                    String user_notification = object.getString("user_notification");
                                    // GLOBALS
                                    global.setDataUserId(user_id);
                                    global.setDataUserNamaLengkap(user_namalengkap);
                                    global.setDataUserNama(user_nama);
                                    global.setDataUserEmail(user_email);
                                    global.setDataUserCerita(user_cerita);
                                    global.setDataUserAvatar(user_avatar);
                                    global.setDataUserPoint(user_point);
                                    // Start notification service
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        scheduleJob();
                                    }
                                    // SESSION
                                    session.setUserId(user_id);
                                    session.setUserNamaLengkap(user_namalengkap);
                                    session.setUserNama(user_nama);
                                    session.setUserEmail(email);
                                    session.setUserCerita(user_cerita);
                                    session.setUserPassword(password);
                                    session.setUserAvatar(user_avatar);
                                    session.setUserAvatarAlternative(user_avatar_alternative);
                                    session.setUserPoint(user_point);
                                    session.setUserNotification(user_notification);
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            } else {
                                login.setText("Not Valid!");
                                loginButton.setText("Start Again");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(view, "JSON error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        login.setText("Something Wrong!");
                        loginButton.setText("Start Again");
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
                }) {
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("device_name", DEVICE_NAME);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openSignup(View view) {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
        super.onBackPressed();
    }
}
