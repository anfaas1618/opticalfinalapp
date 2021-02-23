package wrteam.ekart.shop.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import wrteam.ekart.shop.R;
import wrteam.ekart.shop.activity.DrawerActivity;
import wrteam.ekart.shop.activity.LoginActivity;
import wrteam.ekart.shop.helper.AndroidMultiPartEntity;
import wrteam.ekart.shop.helper.ApiConfig;
import wrteam.ekart.shop.helper.Constant;
import wrteam.ekart.shop.helper.Session;
import wrteam.ekart.shop.helper.VolleyCallback;
import wrteam.ekart.shop.ui.CircleTransform;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static wrteam.ekart.shop.helper.ApiConfig.createJWT;

public class ProfileFragment extends Fragment {

    public static final int SELECT_FILE = 110;
    public static final int REQUEST_IMAGE_CAPTURE = 100;
    public static final int REQUEST_CROP_IMAGE = 120;
    public final int reqWritePermission = 2;
    final File output = null;
    public ImageView imgProfile;
    public FloatingActionButton fabProfile;
    public ProgressBar progressBar;
    View root;
    TextView txtchangepassword;
    Session session;
    Button btnsubmit;
    Activity activity;
    EditText edtname, edtemail, edtMobile;
    String currentPhotoPath;
    Uri fileUri, imageUri;
    String filePath = null;
    File sourceFile;
    long totalSize = 0;

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

        imgProfile = root.findViewById(R.id.imgProfile);

        Picasso.get()
                .load(session.getData(Constant.PROFILE))
                .fit()
                .centerInside()
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .transform(new CircleTransform())
                .into(imgProfile);

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        txtchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("fromto", "").putExtra(Constant.FROM, "changepsw"));
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edtname.getText().toString();
                final String email = edtemail.getText().toString();
                final String mobile = edtMobile.getText().toString();

                if (ApiConfig.CheckValidattion(name, false, false)) {
                    edtname.requestFocus();
                    edtname.setError(getString(R.string.enter_name));
                } else if (ApiConfig.CheckValidattion(email, false, false)) {
                    edtemail.requestFocus();
                    edtemail.setError(getString(R.string.enter_email));
                } else if (ApiConfig.CheckValidattion(email, true, false)) {
                    edtemail.requestFocus();
                    edtemail.setError(getString(R.string.enter_valid_email));
                } else if (ApiConfig.isConnected(activity)) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Constant.TYPE, Constant.EDIT_PROFILE);
                    params.put(Constant.ID, session.getData(Constant.ID));
                    params.put(Constant.NAME, name);
                    params.put(Constant.EMAIL, email);
                    params.put(Constant.MOBILE, mobile);
                    params.put(Constant.LONGITUDE, session.getCoordinates(Constant.LONGITUDE));
                    params.put(Constant.LATITUDE, session.getCoordinates(Constant.LATITUDE));
                    params.put(Constant.FCM_ID, session.getData(Constant.FCM_ID));
                    //System.out.println("====update res " + params.toString());
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            //System.out.println ("=================* " + response);
                            if (result) {
                                try {
                                    JSONObject objectbject = new JSONObject(response);
                                    if (!objectbject.getBoolean(Constant.ERROR)) {
                                        session.setData(Constant.NAME, name);
                                        session.setData(Constant.EMAIL, email);
                                        session.setData(Constant.MOBILE, mobile);
                                        DrawerActivity.tvName.setText(name);

                                    }
                                    Toast.makeText(activity, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {

                                }
                            }
                        }
                    }, activity, Constant.RegisterUrl, params, true);
                }


            }
        });

        edtname.setText(session.getData(Constant.NAME));
        edtemail.setText(session.getData(Constant.EMAIL));
        edtMobile.setText(session.getData(Constant.MOBILE));

        return root;
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


    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.from_library), getString(R.string.from_camera), getString(R.string.cancel)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.from_camera))) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TUKUTUKU_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(activity);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(activity);
            } else if (requestCode == REQUEST_CROP_IMAGE) {
                CropImage.activity(FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", output)).start(activity);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
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

                    Picasso.get()
                            .load(session.getData(Constant.PROFILE))
                            .fit()
                            .centerInside()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .transform(new CircleTransform())
                            .into(DrawerActivity.imgProfile);
                }
                Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {

            }

            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }


}