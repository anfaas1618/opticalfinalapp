package com.mibtech.opticalstorm.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mibtech.opticalstorm.R;
import com.mibtech.opticalstorm.activity.MainActivity;
import com.mibtech.opticalstorm.helper.Constant;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LensPowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LensPowerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG="LensPowerFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView tvConfirmLens ;
    LinearLayout llcheckbox;
    public CheckBox chSingleVision,chBifocal,chProgressive;
    public CheckBox ch999,ch1399,ch1199,ch1299,ch1699,ch1899,ch1999,ch2899,ch2699,chZero;
    public EditText RSPH,RCYL,RAXIS,RVA ,LSPH,LCYL,LAXIS,LVA,additional,intermediate;
    TextView  tvSubTotal;
    ImageView IDProf;
    Button Upload_Btn;
    ProgressDialog dialog;
    String tRSPH="null";
    String tRCYL="null";
    String tRAXIS="null";
    String tRVA="null";

    String tLSPH="null";
    String tLCYL="null";
    String tLAXIS="null";
    String tLVA="null";

    String tintermediate="null";
    String tadditional="null";
    Uri urlupload;
    Uri imageuri;

    public LinearLayout single_vision_ll,bifocal_ll,progressive_ll;
    public RelativeLayout relspec;
    Bitmap bitmap = null;
    public  int lens_val;
    public String lens_price,lens_name;
    View root;
    float temp_price=0;
    public LensPowerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LensPowerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LensPowerFragment newInstance(String param1, String param2) {
        LensPowerFragment fragment = new LensPowerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            imageuri=selectedImage;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                IDProf.setImageBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_lens_power, container, false);
        getAllWidgets(root);
        tvSubTotal.setText(String.format("%s", Constant.FLOAT_TOTAL_AMOUNT));
        tvConfirmLens.setOnClickListener(v->ProceedCheckOut());
        IDProf.setOnClickListener(v -> selectImage());
        Upload_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "onClick: "+"hello");
                dialog = ProgressDialog.show(getActivity(), "",
                        "uploading image. Please wait...", true);
                uploadFirebase();
                ;
            }

            private void uploadFirebase() {
                if (imageuri!=null)
                {

                    // Create a storage reference from our app
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReferenceProfilePic = firebaseStorage.getReference();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();
                    final StorageReference imageRef = storageReferenceProfilePic.child("uploads" + "/" + ts + ".jpg");


// Create a reference to 'images/mountains.jpg'

                    imageRef.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "sucess", Toast.LENGTH_SHORT).show();


                            try {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.i("urlis",uri.toString());
                                        Toast.makeText(getActivity(), "got it do it now", Toast.LENGTH_SHORT).show();
                                        urlupload=uri;
                                        dialog.dismiss();
                                    }
                                });
                            }
                            catch ( Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            }
        });
        chSingleVision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chSingleVision.isChecked())
                {
                    Log.i(TAG, "onClick: "+"check");
                    single_vision_ll.setVisibility(View.VISIBLE);
                    chBifocal.setChecked(false);
                    chProgressive.setChecked(false);
                    progressive_ll.setVisibility(View.GONE);
                    bifocal_ll.setVisibility(View.GONE);
                    ch1299.setChecked(false);
                    ch1699.setChecked(false);
                    ch1899.setChecked(false);
                    ch1999.setChecked(false);
                    ch2899.setChecked(false);
                    ch2699.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));
                }
                if (!chSingleVision.isChecked())
                {
                    Log.i(TAG, "onClick: "+"not check");
                    single_vision_ll.setVisibility(View.GONE);
                    ch999.setChecked(false);
                    ch1399.setChecked(false);
                    ch1199.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));
                }

            }
        });
        chBifocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chBifocal.isChecked())
                {
                    Log.i(TAG, "onClick: "+"check");
                    bifocal_ll.setVisibility(View.VISIBLE);
                    chSingleVision.setChecked(false);
                    chProgressive.setChecked(false);
                    single_vision_ll.setVisibility(View.GONE);
                    progressive_ll.setVisibility(View.GONE);
                    ch999.setChecked(false);
                    ch1399.setChecked(false);
                    ch1199.setChecked(false);
                    ch1999.setChecked(false);
                    ch2899.setChecked(false);
                    ch2699.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));
                }
                if (!chBifocal.isChecked())
                {
                    Log.i(TAG, "onClick: "+"not check");
                    bifocal_ll.setVisibility(View.GONE);
                    ch1299.setChecked(false);
                    ch1699.setChecked(false);
                    ch1899.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));

                }

            }
        });
        chProgressive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chProgressive.isChecked())
                {
                    Log.i(TAG, "onClick: "+"check");
                    progressive_ll.setVisibility(View.VISIBLE);
                    chSingleVision.setChecked(false);
                    chBifocal.setChecked(false);
                    single_vision_ll.setVisibility(View.GONE);
                    bifocal_ll.setVisibility(View.GONE);
                    ch1299.setChecked(false);
                    ch1699.setChecked(false);
                    ch1899.setChecked(false);
                    ch999.setChecked(false);
                    ch1399.setChecked(false);
                    ch1199.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));

                }
                if (!chProgressive.isChecked())
                {
                    Log.i(TAG, "onClick: "+"not check");
                    progressive_ll.setVisibility(View.GONE);
                    ch1999.setChecked(false);
                    ch2899.setChecked(false);
                    ch2699.setChecked(false);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));

                }

            }
        });
        chZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chZero.isChecked())
                {
                    Log.i(TAG, "onClick: "+"check");
                    relspec.setVisibility(View.GONE);
                    temp_price=0;
                    tvSubTotal.setText(String.format("₹ %s", Constant.FLOAT_TOTAL_AMOUNT));
                }
                if (!chZero.isChecked())
                {
                    Log.i(TAG, "onClick: "+"not check");
                    relspec.setVisibility(View.VISIBLE);

                }

            }
        });


        ch999.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch999.isChecked())
                {
                    lens_price=    ch999.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch999.isChecked())
                {   lens_price=    ch999.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));

                }

            }
        });
        ch1399.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1399.isChecked())
                {
                    lens_price=    ch1399.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1399.isChecked())
                { lens_price=    ch1399.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        ch1199.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1199.isChecked())
                {
                    lens_price=    ch1199.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1199.isChecked())
                {
                    lens_price=    ch1199.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        ch1299.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1299.isChecked())
                {
                    lens_price=    ch1299.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1299.isChecked())
                {
                    lens_price=    ch1299.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });

        ch1699.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1699.isChecked())
                {
                    lens_price=    ch1699.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1699.isChecked())
                {
                    lens_price=    ch1699.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });

        ch1899.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1899.isChecked())
                {
                    lens_price=    ch1899.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1899.isChecked())
                {
                    lens_price=    ch1899.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        ch1999.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch1999.isChecked())
                {
                    lens_price=    ch1999.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch1999.isChecked())
                {
                    lens_price=    ch1999.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        ch2899.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch2899.isChecked())
                {
                    lens_price=    ch2899.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch2899.isChecked())
                {
                    lens_price=    ch2899.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        ch2699.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch2699.isChecked())
                {
                    lens_price=    ch2699.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", TempTotal(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }
                if (!ch2699.isChecked())
                {
                    lens_price=    ch2699.getTag().toString().trim();
                    tvSubTotal.setText(String.format("₹ %s", DeductPrice(lens_price,Constant.FLOAT_TOTAL_AMOUNT)));
                }

            }
        });
        return root;
    }

    private String DeductPrice(String lens_price, double floatTotalAmount) {
        temp_price-=  Float.parseFloat(lens_price);
        return String.valueOf(temp_price+floatTotalAmount);
    }

    private String TempTotal(String lens_price, double floatTotalAmount) {
        temp_price+=  Float.parseFloat(lens_price);
        return String.valueOf(temp_price+floatTotalAmount);
    }

    public static final int GET_FROM_GALLERY = 3;
    private void selectImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    private void ProceedCheckOut() {
        String lens_name_build="";
        Fragment fragment = new AddressListFragment();
        if (ch999.isChecked())
        {
            lens_price=    ch999.getTag().toString().trim();
            lens_name="single vision - Anti-Glare";
            lens_name_build=lens_name+" + "+lens_name_build;
        }
        if (ch1399.isChecked())
        {
            lens_price=    ch1399.getTag().toString().trim();
            lens_name="single vision - blue-block (computer Glasses)";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        if (ch1199.isChecked())
        {
            lens_price=    ch1199.getTag().toString().trim();
            lens_name="single vision - photo Chromics (anti glare)";
            lens_name_build=lens_name_build+" + "+lens_name;

        }
        if (ch1299.isChecked())
        {
            lens_price=    ch1299.getTag().toString().trim();
            lens_name="Bifocals - Anti-Glare";
            lens_name_build=lens_name_build+" + "+lens_name;

        }
        if (ch1699.isChecked())
        {
            lens_price=    ch1699.getTag().toString().trim();
            lens_name="Bifocals - blue-block (computer Glasses)";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        if (ch1899.isChecked())
        {
            lens_price=    ch1899.getTag().toString().trim();
            lens_name="Bifocals - photo Chromics (anti glare)";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        if (ch1999.isChecked())
        {
            lens_price=    ch1999.getTag().toString().trim();
            lens_name="Progressive - Anti-Glare";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        if (ch2899.isChecked())
        {
            lens_price=    ch2899.getTag().toString().trim();
            lens_name="Progressive - blue-block (computer Glasses)";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        if (ch2699.isChecked())
        {
            lens_price=    ch2699.getTag().toString().trim();
            lens_name="Progressive - photo Chromics (anti glare)";
            lens_name_build=lens_name_build+" + "+lens_name;
        }
        //specs values
        if (!chZero.isChecked())
        {
            tRSPH=RSPH.getText().toString().trim();
            tRCYL= RCYL.getText().toString().trim();
            tRAXIS=RAXIS.getText().toString().trim();
            tRVA=RVA.getText().toString().trim();

            tLSPH=LSPH.getText().toString().trim();
            tLCYL=LCYL.getText().toString().trim();
            tLAXIS=LAXIS.getText().toString().trim();
            tLVA=LVA.getText().toString().trim();

            tintermediate=intermediate.getText().toString().trim();
            tadditional=additional.getText().toString().trim();

        }


        try {
            lens_val=Integer.parseInt(lens_price);
        }catch (Exception e)
        {
            lens_val=0;
        }
        Bundle bundle = new Bundle();

        Constant.FLOAT_TOTAL_AMOUNT+=temp_price;
//       bundle.putDouble("subtotal", Double.parseDouble("" + "subtotal"));
//        bundle.putDouble("total", Double.parseDouble("" + Constant.FLOAT_TOTAL_AMOUNT));
//        bundle.putDouble("pCodeDiscount", Double.parseDouble("" + pCodeDiscount));
//        bundle.putString("pCode", pCode);
//        bundle.putStringArrayList("variantIdList", variantIdList);
//        bundle.putStringArrayList("qtyList", qtyList);
//        bundle.putString(Constant.FROM, "process");
//        PaymentFragment.paymentMethod = "";
//        PaymentFragment.deliveryTime = "";
//        PaymentFragment.deliveryDay = "";
        bundle.putString("lens_prices", String.valueOf(temp_price));//HERE
        bundle.putString("lens_name",lens_name_build);//HERE
        bundle.putString("RSPH",tRSPH);//HERE
        bundle.putString("RCYL",tRCYL);//HERE
        bundle.putString("RAXIS",tRAXIS);//HERE
        bundle.putString("RVA",tRVA);//HERE
        bundle.putString("LSPH",tLSPH);//HERE
        bundle.putString("LCYL",tLCYL);//HERE
        bundle.putString("LAXIS",tLAXIS);//HERE
        bundle.putString("LVA",tLVA);//HERE
        bundle.putString("intermediate",tintermediate);//HERE
        bundle.putString("additional",tadditional);//HERE
        bundle.putString(Constant.FROM, getArguments().getString(Constant.FROM));
        bundle.putDouble("total",getArguments().getDouble("total") );
        if (urlupload!=null)
            bundle.putString("imageUrl",urlupload.toString());
        else
            bundle.putString("imageUrl","no URL uploaded");

        System.out.println("=====params " + bundle.toString());
        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }

    private void getAllWidgets(View root) {
        tvConfirmLens=root.findViewById(R.id.tvConfrimLens);
        //checkboxes//
        ch999=root.findViewById(R.id.chAntiGlare1);
        ch1399=root.findViewById(R.id.chBlueBlock1);
        ch1199=root.findViewById(R.id.chPhotoChromics1);
        ch1299=root.findViewById(R.id.chAntiGlare2);
        ch1699=root.findViewById(R.id.chBlueBlock2);
        ch1899=root.findViewById(R.id.chPhotoChromics2);
        ch1999=root.findViewById(R.id.chAntiGlare3);
        ch2899=root.findViewById(R.id.chBlueBlock3);
        ch2699=root.findViewById(R.id.chPhotoChromics3);
        chZero=root.findViewById(R.id.chZeroPower);
        tvSubTotal =root.findViewById(R.id.tvSubTotal);
        llcheckbox=root.findViewById(R.id.llcheckbox);
        //editText
        RSPH=root.findViewById(R.id.RSPH);
        RCYL=root.findViewById(R.id.RCYL);
        RAXIS=root.findViewById(R.id.RAXIS);
        RVA=root.findViewById(R.id.RVA);
        LSPH=root.findViewById(R.id.LSPH);
        LCYL=root.findViewById(R.id.LCYL);
        LAXIS=root.findViewById(R.id.LAXIS);
        LVA=root.findViewById(R.id.LVA);
        chSingleVision=root.findViewById(R.id.chSingleVision);
        single_vision_ll=root.findViewById(R.id.single_vision_ll);
        chBifocal=root.findViewById(R.id.chBifocal);
        bifocal_ll=root.findViewById(R.id.bifocal_ll);
        chProgressive=root.findViewById(R.id.chProgressive);
        progressive_ll=root.findViewById(R.id.progressive_ll);
        relspec=root.findViewById(R.id.relspec);
        intermediate=root.findViewById(R.id.intermediate);
        additional=root.findViewById(R.id.additional);

        IDProf=(ImageView)root.findViewById(R.id.IdProf);
        Upload_Btn=(Button)root.findViewById(R.id.UploadBtn);
    }


}