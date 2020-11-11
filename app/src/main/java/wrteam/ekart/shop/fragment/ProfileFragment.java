package wrteam.ekart.shop.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.DrawerActivity;
import wrteam.ekart.shop.helper.AndroidMultiPartEntity;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.AppController;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.ui.CircleTransform;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.createJWT;

public class ProfileFragment extends Fragment {

    public static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static int SELECT_FILE = 110;
    final File output = null;
    public ImageView imgProfile;
    public FloatingActionButton fabProfile;
    public int reqWritePermission = 2;
    public ProgressBar progressBar;
    View root;
    TextView txtchangepassword;
    Session session;
    Button btnsubmit;
    Activity activity;
    Uri fileUri;
    File sourceFile;
    long totalSize = 0;
    EditText edtname, edtemail, edtMobile;
    String filePath = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profile, container, false);
        activity = getActivity();

        edtname = root.findViewById(R.id.edtname);
        edtemail = root.findViewById(R.id.edtemail);
        edtMobile = root.findViewById(R.id.edtMobile);
        btnsubmit = root.findViewById(R.id.btnsubmit);
        txtchangepassword = root.findViewById(R.id.txtchangepassword);
        fabProfile = root.findViewById(R.id.fabProfile);
        progressBar = root.findViewById(R.id.progressBar);

        setHasOptionsMenu(true);

        session = new Session(getContext());

        ApiConfig.getLocation(activity);

        imgProfile = root.findViewById(R.id.imgProfile);

        Picasso.get()
                .load(session.getData(Constant.PROFILE))
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(new CircleTransform())
                .into(imgProfile);

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edtname.getText().toString();
                final String email = edtemail.getText().toString();
                final String mobile = edtMobile.getText().toString();

                if (ApiConfig.CheckValidattion(name, false, false))
                    edtname.setError(getString(R.string.enter_name));
                if (ApiConfig.CheckValidattion(email, false, false))
                    edtemail.setError(getString(R.string.enter_email));
                else if (ApiConfig.CheckValidattion(email, true, false))
                    edtemail.setError(getString(R.string.enter_valid_email));

                else if (AppController.isConnected(activity)) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.TYPE, Constant.EDIT_PROFILE);
                    params.put(Constant.ID, session.getData(Session.KEY_ID));
                    params.put(Constant.NAME, name);
                    params.put(Constant.EMAIL, email);
                    params.put(Constant.MOBILE, mobile);
                    params.put(Constant.LONGITUDE, session.getCoordinates(Session.KEY_LONGITUDE));
                    params.put(Constant.LATITUDE, session.getCoordinates(Session.KEY_LATITUDE));
                    params.put(Constant.FCM_ID, AppController.getInstance().getDeviceToken());
                    //System.out.println("====update res " + params.toString());
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            //System.out.println ("=================* " + response);
                            if (result) {
                                try {
                                    JSONObject objectbject = new JSONObject(response);
                                    if (!objectbject.getBoolean(Constant.ERROR)) {
                                        session.setData(Session.KEY_NAME, name);
                                        session.setData(Session.KEY_EMAIL, email);
                                        session.setData(Session.KEY_MOBILE, mobile);
                                        DrawerActivity.tvName.setText(name);

                                    }
                                    Toast.makeText(activity, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, activity, Constant.RegisterUrl, params, true);
                }


            }
        });

        edtname.setText(session.getData(Session.KEY_NAME));
        edtemail.setText(session.getData(Session.KEY_EMAIL));
        edtMobile.setText(session.getData(Session.KEY_MOBILE));

        return root;
    }


    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.alert));
                    builder.setMessage(activity.getResources().getString(R.string.image_permisison_msg));
                    builder.setPositiveButton(activity.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.from_library), getString(R.string.cancel)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.profile);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_logout).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressQuality(80)
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setAspectRatio(1, 1)
                    .start(activity);

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.activity(FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", output)).start(activity);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }

    class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.RegisterUrl);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                filePath = fileUri.getPath();
                sourceFile = new File(filePath);

//                // Adding file data to http body
                entity.addPart(Constant.AccessKey, new StringBody(Constant.AccessKeyVal));
                entity.addPart(Constant.PROFILE, new FileBody(sourceFile));
                entity.addPart(Constant.USER_ID, new StringBody(session.getData(Constant.ID)));
                entity.addPart(Constant.TYPE, new StringBody(Constant.UPLOAD_PROFILE));


                totalSize = entity.getContentLength();
                httppost.addHeader(Constant.AUTHORIZATION, "Bearer " + createJWT("eKart", "eKart Authentication"));
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }

            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                if (!error) {
                    session.setData(Constant.PROFILE, jsonObject.getString(Constant.PROFILE));

                    Picasso.get()
                            .load(session.getData(Constant.PROFILE))
                            .fit()
                            .centerInside()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .transform(new CircleTransform())
                            .into(imgProfile);

                }
                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }


}