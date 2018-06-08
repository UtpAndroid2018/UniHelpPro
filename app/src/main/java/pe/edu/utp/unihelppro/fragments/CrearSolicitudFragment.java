package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.Connect;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrearSolicitudFragment.OnActionCrearSolicitudFragmentListener} interface
 * to handle interaction events.
 */
public class CrearSolicitudFragment extends Fragment implements SelectImageDialogFragment.Listener {

    private OnActionCrearSolicitudFragmentListener mListener;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/UniHelpPro";
    private static final String IMAGE_DIRECTORY_NAME = "UniHelpPro";

    public CrearSolicitudFragment() {

    }

    public static CrearSolicitudFragment newInstance() {

        Bundle args = new Bundle();
        CrearSolicitudFragment fragment = new CrearSolicitudFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_solicitud, container, false);
        registrarIncidente( view );

        SelectImageDialogFragment selectImageDialogFragment = SelectImageDialogFragment.newInstance(2);
        selectImageDialogFragment.show( getChildFragmentManager(), "dialog" );
        return view;
    }


    @Override
    public void onItemImageClicked(int position) {
        Log.e("Splash", position + "");
        switch (position) {
            case 0:
                choosePhotoFromGallary();
                break;
            case 1:
                takePhotoFromCamera();
                break;
        }
    }



    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    //Toast.makeText(Connect.getInstance(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Connect.getInstance(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //imageview.setImageBitmap(thumbnail);
            String path = saveImage(thumbnail);
            //Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(Connect.getInstance(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());

            Backendless.Files.Android.upload( myBitmap, Bitmap.CompressFormat.JPEG, 100, f.getName(), IMAGE_DIRECTORY_NAME,
                    new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse( final BackendlessFile backendlessFile ) {
                            Toast.makeText( Connect.getInstance(), backendlessFile.getFileURL(), Toast.LENGTH_SHORT ).show();
                        }

                        @Override
                        public void handleFault( BackendlessFault backendlessFault ) {
                            Toast.makeText( Connect.getInstance(), backendlessFault.toString(), Toast.LENGTH_SHORT ).show();
                        }
                    }
            );
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void registrarIncidente(View view) {
        HashMap solicitud = new HashMap();
        solicitud.put( "descripcion", "Esta es una nueva solicitud de cotización" );
        solicitud.put( "publica", true );
        solicitud.put( "sede", "Lima - Centro" );
        solicitud.put( "tipo", "" );

        // save object asynchronously
        /*
        Backendless.Persistence.of( "Contact" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse( Map response ) {
                //Toast.makeText(mContext, "Registrado", Toast.LENGTH_SHORT).show();
            }

            public void handleFault( BackendlessFault fault ) {
                //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionCrearSolicitudFragmentListener) {
            mListener = (OnActionCrearSolicitudFragmentListener) context;
        } else {
            //throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnActionCrearSolicitudFragmentListener {
        void onCrearFail();
        void onCrearSuccess();
    }
}
