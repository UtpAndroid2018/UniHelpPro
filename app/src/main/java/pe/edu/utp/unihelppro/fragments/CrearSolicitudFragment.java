package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushBroadcastMask;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pe.edu.utp.unihelppro.Defaults;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.activities.MainActivity;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;

import static android.app.Activity.RESULT_CANCELED;


public class CrearSolicitudFragment extends Fragment implements RecordFragment.OnCaptureAudio, SelectImageDialogFragment.Listener {

    private OnActionCrearSolicitudFragmentListener mListener;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/UniHelpPro";
    private static final String IMAGE_DIRECTORY_NAME = "UniHelpPro";

    Spinner inputSede;
    Spinner inputPabellon;
    Spinner inputAula;
    Spinner inputCategoria;
    EditText inputDescripcion;
    TextView inputNombre;
    Button inputRegistrar;
    Button btnFoto;
    Button btnAudio;
    ImageView previewImageView;
    LinearLayout linearInputNombre;
    Switch inputPublico;
    Context mContext;
    private String currentPathImage = "";
    private String currentPathAudio = "";
    private Bitmap currentBMImage;

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
        inputSede = ( Spinner ) view.findViewById(R.id.inputSede);
        inputPabellon = ( Spinner ) view.findViewById(R.id.inputPabellon);
        inputAula = ( Spinner ) view.findViewById(R.id.inputAula);
        inputCategoria = ( Spinner ) view.findViewById(R.id.inputCategoria);
        inputDescripcion = ( EditText ) view.findViewById(R.id.inputDescripcion);
        inputNombre = ( TextView ) view.findViewById(R.id.inputNombre);
        inputRegistrar = ( Button ) view.findViewById(R.id.inputRegistrar);
        btnFoto = ( Button ) view.findViewById(R.id.btnFoto);
        btnAudio = ( Button ) view.findViewById(R.id.btnAudio);
        previewImageView = ( ImageView ) view.findViewById(R.id.previewImageView);
        linearInputNombre = ( LinearLayout ) view.findViewById(R.id.linearInputNombre);
        inputPublico = (Switch) view.findViewById(R.id.inputPublico);

        mContext = getActivity();

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageDialogFragment selectImageDialogFragment = SelectImageDialogFragment.newInstance(2);
                selectImageDialogFragment.show( getChildFragmentManager(), "dialog" );
            }
        });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecord( );
            }
        });

        inputRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAllMedia();
            }
        });

        return view;
    }


    private void registerToChanel(final String incidenteId ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Backendless.Messaging.registerDevice(Defaults.gcmSenderID, incidenteId, new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        //Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void showRecord() {
        RecordFragment rf = RecordFragment.newInstance(  );
        rf.setListener(this);
        rf.show(getChildFragmentManager(), "dialog");
    }

    private void uploadAudio(final BackendlessFile photoFile ) {
        File f = new File( currentPathAudio );
        Backendless.Files.upload( f, IMAGE_DIRECTORY_NAME,
                new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse( final BackendlessFile backendlessFile ) {
                        registrarIncidente( photoFile, backendlessFile );
                    }
                    @Override
                    public void handleFault( BackendlessFault backendlessFault ) {
                        Toast.makeText( Connect.getInstance(), backendlessFault.toString(), Toast.LENGTH_SHORT ).show();
                    }
                }
        );
    }
    private void uploadPhoto() {
        File f = new File( currentPathImage );
        if( currentBMImage == null ) {
            currentBMImage = BitmapFactory.decodeFile( f.getAbsolutePath() );
        }

        Backendless.Files.Android.upload( currentBMImage, Bitmap.CompressFormat.JPEG, 100, f.getName(), IMAGE_DIRECTORY_NAME,
                new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse( final BackendlessFile backendlessFile ) {
                        if( !currentPathAudio.equals("") ) {
                            uploadAudio( backendlessFile );
                        } else {
                            registrarIncidente( backendlessFile, null );
                        }
                    }

                    @Override
                    public void handleFault( BackendlessFault backendlessFault ) {
                        Toast.makeText( Connect.getInstance(), backendlessFault.toString(), Toast.LENGTH_SHORT ).show();
                    }
                }
        );
    }
    private void uploadAllMedia() {
        if( !currentPathImage.equals("") || currentBMImage != null ) {
            uploadPhoto();
        } else {
            if( !currentPathAudio.equals("") ) {
                uploadAudio( null );
            } else {
                registrarIncidente( null, null );
            }
        }
    }

    @Override
    public void onItemImageClicked(int position) {
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile( );
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "pe.edu.utp.unihelppro.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA);
            }
        }
    }

    private File createImageFile() {
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            currentPathImage = f.getAbsolutePath();
            MediaScannerConnection.scanFile(Connect.getInstance(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());
            return f;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
                    currentPathImage = saveImage(bitmap);

                    previewImageView.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load( new File( currentPathImage ) ).into(previewImageView);

                } catch (IOException e ) {
                    e.printStackTrace();
                    Toast.makeText(Connect.getInstance(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            previewImageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load( new File( currentPathImage ) ).into(previewImageView);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.flush();
            //fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(Connect.getInstance(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());
            currentBMImage = myBitmap;
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private UsuarioBackendless setupUser(BackendlessUser currentUser ) {
        UsuarioBackendless ub = new UsuarioBackendless( currentUser.getObjectId()  );
        ub.save();
        return ub;
    }

    public void registrarIncidente( BackendlessFile photoFile, BackendlessFile audioFile ) {
        HashMap solicitud = new HashMap();
        solicitud.put( "descripcion", inputDescripcion.getText().toString() );
        solicitud.put( "publico", inputPublico.isChecked() );
        solicitud.put( "sede", inputSede.getSelectedItem().toString() );
        solicitud.put( "categoria", inputCategoria.getSelectedItem().toString() );
        solicitud.put( "pabellon", inputPabellon.getSelectedItem().toString() );
        solicitud.put( "aula", inputAula.getSelectedItem().toString() );
        solicitud.put( "fecha", new Date());

        if( photoFile != null ) {
            solicitud.put( "foto", photoFile.getFileURL());
        }
        if( audioFile != null ) {
            solicitud.put( "audio", audioFile.getFileURL());
        }


        Locale LocaleBylanguageTag = Locale.forLanguageTag("es");
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

        long timeInMillis = System.currentTimeMillis();
        String text = TimeAgo.using(timeInMillis, messages);

        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();

        Backendless.Persistence.of( "Incidentes" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedIncidente ) {
                ObjectMapper mapper = new ObjectMapper();
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                final Incidentes incidente = mapper.convertValue(savedIncidente, Incidentes.class);

                if ( currentUser != null ) {
                    Map savedAddress2 = currentUser.getProperties();
                    IDataStore<Map> contactStorage = Backendless.Data.of( "Incidentes" );

                    List<Map> addresses = new ArrayList<Map>();
                    addresses.add( savedAddress2 );
                    contactStorage.setRelation(savedIncidente, "usuarioEmisor:Incidentes:1", addresses, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Toast.makeText(mContext, "Registrado", Toast.LENGTH_SHORT).show();
                            incidente.setUsuarioEmisor( setupUser( currentUser ) );
                            incidente.setupUsers( savedIncidente );
                            incidente.save();

                            registerToChanel( incidente.getObjectId() );
                            MenuItem menuItem = ( (MainActivity) mContext).nvDrawer.getMenu().findItem(R.id.nav_incidentes);
                            ( (MainActivity) mContext).selectDrawerItem(menuItem);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(final BackendlessUser response) {

                            Map savedAddress2 = response.getProperties();
                            IDataStore<Map> contactStorage = Backendless.Data.of( "Incidentes" );

                            List<Map> addresses = new ArrayList<Map>();
                            addresses.add( savedAddress2 );
                            contactStorage.setRelation(savedIncidente, "usuarioEmisor:Incidentes:1", addresses, new AsyncCallback<Integer>() {
                                @Override
                                public void handleResponse(Integer responseRelation) {
                                    Toast.makeText(mContext, "Registrado", Toast.LENGTH_SHORT).show();
                                    incidente.setUsuarioEmisor( setupUser( response ) );
                                    incidente.setupUsers( savedIncidente );
                                    incidente.save();
                                    MenuItem menuItem = ( (MainActivity) mContext).nvDrawer.getMenu().findItem(R.id.nav_incidentes);
                                    ( (MainActivity) mContext).selectDrawerItem(menuItem);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionCrearSolicitudFragmentListener) {
            mListener = (OnActionCrearSolicitudFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnCaptureAudio(String path) {
        currentPathAudio = path;
    }

    public interface OnActionCrearSolicitudFragmentListener {
        void onCrearFail();
        void onCrearSuccess();
    }
}
