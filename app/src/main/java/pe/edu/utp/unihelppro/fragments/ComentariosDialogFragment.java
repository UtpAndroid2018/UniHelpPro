package pe.edu.utp.unihelppro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.activities.MainActivity;
import pe.edu.utp.unihelppro.adapters.ComentarioAdapter;
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;
import pe.edu.utp.unihelppro.models.UsuarioProperties;


public class ComentariosDialogFragment extends DialogFragment {
    private String incidenteId;
    private Context mContext;
    //private Listener mListener;
    private double h = 0.2;
    private EditText inputDescripcion;
    private List<Comentarios> comentariosList = new ArrayList<>();
    private RelativeLayout relative_progressbar;
    private LinearLayout linear_content;
    private RecyclerView recyclerView;
    private ImageButton enviar_comentario;


    public static ComentariosDialogFragment newInstance( String _incidenteId ) {
        final ComentariosDialogFragment fragment = new ComentariosDialogFragment();
        final Bundle args = new Bundle();
        args.putString( "incidenteId", _incidenteId );
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /*
        float width =  (float) (Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().widthPixels * 0.5 );
        float height = (float) ( getContext().getResources().getDisplayMetrics().heightPixels * 0.5);

        Objects.requireNonNull(getDialog().getWindow()).setLayout(  Math.round( width ), Math.round( height ));
        */

        View view = inflater.inflate(R.layout.fragment_comentarios_list_dialog, container, false);

        mContext = getContext();

        recyclerView = (RecyclerView) view.findViewById( R.id.list );
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ComentarioAdapter( new ArrayList<Comentarios>(), getContext() ));

        relative_progressbar = (RelativeLayout) view.findViewById( R.id.relative_progressbar );
        linear_content = (LinearLayout) view.findViewById( R.id.linear_content );

        enviar_comentario = (ImageButton) view.findViewById( R.id.enviar_comentario );
        inputDescripcion = (EditText) view.findViewById( R.id.inputDescripcion );
        enviar_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( inputDescripcion.getText().toString().equals("") ) {
                    Toast.makeText( getContext() , "Debes ingresar un comentario", Toast.LENGTH_SHORT).show();
                    inputDescripcion.requestFocus();
                } else {
                    enviar_comentario.setEnabled(false);
                    publishComent( inputDescripcion.getText().toString() );
                }

            }
        });
        getComents( view );
        return view;
    }

    private UsuarioBackendless setupUser(BackendlessUser currentUser ) {
        UsuarioBackendless ub = new UsuarioBackendless( currentUser.getObjectId()  );
        ub.save();
        return ub;
    }
    private UsuarioProperties setupUserMap(BackendlessUser currentUser ) {
        UsuarioBackendless ub = new UsuarioBackendless( currentUser.getObjectId()  );
        ub.setupUser( currentUser );
        //ub.save();
        UsuarioProperties usuarioProperties = new UsuarioProperties();
        usuarioProperties.setProperties( ub );
        return usuarioProperties;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateViewComents () {
        linear_content.setVisibility(View.VISIBLE);
        relative_progressbar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new ComentarioAdapter( comentariosList, mContext ));
    }

    private void setRelations(final BackendlessUser currentUser, final Map mapData, String tablaName, String relation, final Map savedComentario, final boolean _final ) {
        IDataStore<Map> contactStorage = Backendless.Data.of( tablaName );

        List<Map> addresses = new ArrayList<Map>();
        addresses.add( mapData );
        contactStorage.setRelation(savedComentario, relation, addresses, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {
                Gson gson = new Gson();
                if( _final ) {
                    String json = gson.toJson( savedComentario );
                    Comentarios comentario = gson.fromJson(json, Comentarios.class);
                    Incidentes incidente = new Incidentes( incidenteId );
                    incidente.getData();
                    comentario.setIncidente( incidente );
                    comentario.setUsuario( setupUserMap( currentUser ) );
                    comentario.save();
                    comentariosList.add( 0, comentario );
                    updateViewComents();

                    Toast.makeText(mContext, "Comentario publicado", Toast.LENGTH_SHORT).show();
                    inputDescripcion.setText("");
                    enviar_comentario.setEnabled(true);
                } else {
                    Backendless.Data.of( "Incidentes" ).findById( incidenteId, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(final Map response) {
                            setRelations( currentUser, response ,"Comentarios" , "incidente:Comentarios:1", savedComentario, true );
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(mContext, "Ocurrió un error al publicar el comentario", Toast.LENGTH_SHORT).show();
                            enviar_comentario.setEnabled(true);
                        }
                    });
                }
                //dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(mContext, "Ocurrió un error al publicar el comentario", Toast.LENGTH_SHORT).show();
                enviar_comentario.setEnabled(true);
            }
        });
    }

    private void publishComent( String textComentario ) {
        HashMap solicitud = new HashMap();
        solicitud.put( "descripcion", textComentario );
        solicitud.put( "fecha", new Date());

        //incidente
        //usuario

        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();

        hideKeyboardFrom( mContext, inputDescripcion );
        Backendless.Persistence.of( "Comentarios" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedComentario ) {
                ObjectMapper mapper = new ObjectMapper();
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                final Comentarios comentarios = mapper.convertValue(savedComentario, Comentarios.class);

                if ( currentUser != null ) {
                    Map userMap = currentUser.getProperties();
                    setRelations( currentUser, userMap,"Comentarios" , "usuario:Comentarios:1", savedComentario, false );
                } else {
                    Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(final BackendlessUser response) {
                            Map userMap = response.getProperties();
                            setRelations( response, userMap,"Comentarios" , "usuario:Comentarios:1", savedComentario, false );
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(mContext, "Ocurrió un error al publicar el comentario", Toast.LENGTH_SHORT).show();
                            enviar_comentario.setEnabled(true);
                        }
                    });
                }
            }

            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(mContext, "Ocurrió un error al publicar el comentario", Toast.LENGTH_SHORT).show();
                enviar_comentario.setEnabled(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAutoH( h );
    }

    private void setupAutoH( double h ) {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();

        float width =  (float) (Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().widthPixels * 0.95 );
        float height = (float) ( getContext().getResources().getDisplayMetrics().heightPixels * h);

        params.width = (int) width;//ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) height;//ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FixedDialog);
        if (getArguments() != null) {
            incidenteId = getArguments().getString("incidenteId", "");
            if( incidenteId.equals("") ) {
                dismiss();
            }
        } else {
            dismiss();
        }
        //int width = getContext().getResources().getDisplayMetrics().widthPixels;
        //int height = getContext().getResources().getDisplayMetrics().heightPixels;
        //getDialog().getWindow().setLayout(width, height);

    }

    private void getComents(final View view ) {
        IDataStore<Map> incidentesStorage = Backendless.Data.of( "Comentarios" );
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "incidente='"+ incidenteId +"'" );
        queryBuilder.setSortBy( "-fecha" );
        queryBuilder.setPageSize( 20 );
        incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse( List<Map> maps ) {
                comentariosList = new ArrayList<>();
                for ( Map map: maps) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson( map );
                        Comentarios comentario = gson.fromJson(json, Comentarios.class);
                        comentario.save();
                        comentariosList.add( comentario );

                    } catch ( IllegalArgumentException exception ) {
                        Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                updateViewComents();
                /*
                linear_content.setVisibility(View.VISIBLE);
                relative_progressbar.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(new ComentarioAdapter( comentariosList, mContext ));
                */
                h = 0.90;
                setupAutoH( h );
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            //mListener = (Listener) parent;
        } else {
            //mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        //mListener = null;
        super.onDetach();
    }
}
