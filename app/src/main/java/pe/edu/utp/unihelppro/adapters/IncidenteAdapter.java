package pe.edu.utp.unihelppro.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.activities.MainActivity;
import pe.edu.utp.unihelppro.fragments.AsignarIncidente;
import pe.edu.utp.unihelppro.fragments.CalificarIncidente;
import pe.edu.utp.unihelppro.fragments.ComentariosDialogFragment;
import pe.edu.utp.unihelppro.fragments.IncidenteFragment;
import pe.edu.utp.unihelppro.fragments.ReportarIncidente;
import pe.edu.utp.unihelppro.models.Calificaciones;
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.Likes;
import pe.edu.utp.unihelppro.models.Reportados;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;
import pe.edu.utp.unihelppro.models.UsuarioProperties;

import static pe.edu.utp.unihelppro.Connect.getContext;

public class IncidenteAdapter extends RecyclerView.Adapter<IncidenteAdapter.ViewHolder> implements CalificarIncidente.OnCalificarListener, PopupMenu.OnMenuItemClickListener, ReportarIncidente.OnReportarListener {
    private View mView;
    private List<Incidentes> incidentes;
    private final Context mContext;
    private int _position = -1;
    private static FragmentManager fragmentManager;
    private ProgressDialog progressDialog;
    private Incidentes inc;

    public IncidenteAdapter(List<Incidentes> incidentesList, Context mContext) {
        this.incidentes = incidentesList;
        this.mContext = mContext;
        fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
    }
    public void updateData(List<Incidentes> _incidentes) {
        incidentes.clear();
        incidentes.addAll( _incidentes );
        notifyDataSetChanged();
    }
    private void removeItem(int position) {
        incidentes.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_incidente, parent, false);
        return new IncidenteAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.incidentes.size();
    }

    private UsuarioProperties setupUserMap(BackendlessUser currentUser ) {
        UsuarioBackendless ub = new UsuarioBackendless( currentUser.getObjectId()  );
        ub.setupUser( currentUser );
        UsuarioProperties usuarioProperties = new UsuarioProperties();
        usuarioProperties.setProperties( ub );
        return usuarioProperties;
    }

    private void setRelations(final BackendlessUser currentUser, final Map mapData, final String tablaName, String relation, final Map mapSaved, final Reportados savedReporte, final Calificaciones savedCalifica, final Likes savedLike, final boolean _final, final int idView ) {
        IDataStore<Map> contactStorage = Backendless.Data.of( tablaName );
        List<Map> addresses = new ArrayList<Map>();
        addresses.add( mapData );
        contactStorage.setRelation(mapSaved, relation, addresses, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {
                Gson gson = new Gson();
                if( _final ) {
                    Incidentes incidente = new Incidentes( inc.getObjectId() );
                    incidente.getData();
                    switch ( idView ){
                        case R.id.btnMeGusta:
                            savedLike.setIncidente( incidente );
                            savedLike.setUsuario( setupUserMap( currentUser ) );
                            savedLike.save();
                            dismmisProgreesLoading();
                            Toast.makeText(mContext, "Te gusta el incidente", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_calificar:
                            savedCalifica.setIncidente( incidente );
                            savedCalifica.setUsuarioEmisor( setupUserMap( currentUser ) );
                            savedCalifica.save();
                            dismmisProgreesLoading();
                            Toast.makeText(mContext, "Incidente calificado", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_reportar:
                            savedReporte.setIncidente( incidente );
                            savedReporte.setUsuarioEmisor( setupUserMap( currentUser ) );
                            savedReporte.save();
                            dismmisProgreesLoading();
                            Toast.makeText(mContext, "Incidente reportado", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Map<String, String> solicitud = new HashMap<>();
                    solicitud.put( "objectId", inc.getObjectId() );
                    setRelations( currentUser, solicitud,tablaName , "incidente:"+ tablaName +":1", mapSaved, savedReporte, savedCalifica, savedLike,true, idView );
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al registrar la acción", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReportar(Reportados reporte) {
        loadProgreesUploadLoading();
        HashMap solicitud = new HashMap();
        solicitud.put( "calificacion", reporte.getCalificacion() );
        solicitud.put( "descripcion", reporte.getDescripcion() );
        solicitud.put( "fecha", new Date());
        //reporte.save();
        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();

        Backendless.Persistence.of( "UsuariosReportados" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedReporte ) {
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Gson gson = new Gson();
                String json = gson.toJson( savedReporte );
                final Reportados reportado = gson.fromJson(json, Reportados.class);
                reportado.save();
                if ( currentUser != null ) {
                    Map userMap = currentUser.getProperties();
                    setRelations( currentUser, userMap,"UsuariosReportados" , "usuarioEmisor:UsuariosReportados:1", savedReporte, reportado, null, null, false, R.id.action_reportar );
                } else {
                    Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(final BackendlessUser response) {
                            Map userMap = response.getProperties();
                            setRelations( response, userMap,"UsuariosReportados" , "usuarioEmisor:UsuariosReportados:1", savedReporte, reportado, null, null, false, R.id.action_reportar );
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            dismmisProgreesLoading();
                            Toast.makeText(mContext, "Ocurrió un error al reportar el inicidente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            public void handleFault( BackendlessFault fault ) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al reportar el inicidente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dismmisProgreesLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    private void loadProgreesUploadLoading() {
        progressDialog = ProgressDialog.show(mContext, null, mContext.getResources().getString(R.string.reportando_incidente), true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onCalificar(Calificaciones calificar) {
        loadProgreesUploadLoading();
        progressDialog.setMessage( mContext.getResources().getString(R.string.calificando_incidente ) );
        HashMap solicitud = new HashMap();
        solicitud.put( "calificacion", calificar.getCalificacion() );
        solicitud.put( "descripcion", calificar.getDescripcion() );
        solicitud.put( "fecha", new Date());
        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        Backendless.Persistence.of( "Calificaciones" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedCalifica ) {
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Gson gson = new Gson();
                String json = gson.toJson( savedCalifica );
                final Calificaciones calificado = gson.fromJson(json, Calificaciones.class);
                calificado.save();
                if ( currentUser != null ) {
                    Map userMap = currentUser.getProperties();
                    setRelations( currentUser, userMap,"Calificaciones" , "usuarioEmisor:Calificaciones:1", savedCalifica, null, calificado, null, false, R.id.action_calificar );
                } else {
                    Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(final BackendlessUser response) {
                            Map userMap = response.getProperties();
                            setRelations( response, userMap,"Calificaciones" , "usuarioEmisor:Calificaciones:1", savedCalifica, null, calificado, null, false, R.id.action_calificar );
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            dismmisProgreesLoading();
                            Toast.makeText(mContext, "Ocurrió un error al calificar el incidente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            public void handleFault( BackendlessFault fault ) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al calificar el incidente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView incidenteNombreUsuario;
        private TextView incidenteMeGustas;
        private TextView incidenteComentarios;
        private TextView incidenteFecha;
        private TextView incidenteContenido;
        private ImageView incidenteImagen;
        private LinearLayout linear_reproducir;
        private ImageView incidenteEditar;
        private ImageView incidenteBoton;

        private Button btnMeGusta;
        private Button btnComentar;
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            linear_reproducir = (LinearLayout) itemView.findViewById(R.id.linear_reproducir);
            incidenteComentarios = (TextView) itemView.findViewById(R.id.incidenteComentarios);
            incidenteMeGustas = (TextView) itemView.findViewById(R.id.incidenteMeGustas);
            incidenteNombreUsuario = (TextView) itemView.findViewById(R.id.incidenteNombreUsuario);
            incidenteFecha = (TextView) itemView.findViewById(R.id.incidenteFecha);
            incidenteContenido = (TextView) itemView.findViewById(R.id.incidenteContenido);
            incidenteImagen = (ImageView) itemView.findViewById(R.id.incidenteImagen);

            incidenteEditar = (ImageView) itemView.findViewById(R.id.incidenteEditar);
            incidenteBoton = (ImageView) itemView.findViewById(R.id.incidenteBoton);

            btnMeGusta = (Button) itemView.findViewById(R.id.btnMeGusta);
            btnComentar = (Button) itemView.findViewById(R.id.btnComentar);
            incidenteBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup( view );
                }
            });
        }
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_editar:
                return true;
            case R.id.action_asignar:
                AsignarIncidente asignarIncidente = AsignarIncidente.newInstance("", "");
                asignarIncidente.show( fragmentManager , "dialog" );
                return true;
            case R.id.action_calificar:
                CalificarIncidente calificarIncidente = CalificarIncidente.newInstance();
                calificarIncidente.setmListener( this );
                calificarIncidente.show( fragmentManager , "dialog" );
                return true;
            case R.id.action_reportar:
                ReportarIncidente reportarIncidente = ReportarIncidente.newInstance();
                reportarIncidente.setmListener( this );
                reportarIncidente.show( fragmentManager , "dialog" );
                return true;
            default:
                return false;
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main);
        popup.show();
    }

    private void registrarLike(){
        HashMap solicitud = new HashMap();
        Backendless.Persistence.of( "Likes" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedLikes ) {
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Gson gson = new Gson();
                String json = gson.toJson( savedLikes );
                final Likes like = gson.fromJson(json, Likes.class);
                like.save();
                Map userMap = currentUser.getProperties();
                setRelations( currentUser, userMap,"Likes" , "usuario:Likes:1", savedLikes, null, null, like, false, R.id.btnMeGusta );
            }

            public void handleFault( BackendlessFault fault ) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al registrar el me gusta", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ActualizarIncidenteLike(){
        HashMap solicitud = new HashMap();
        solicitud.put( "megustas", inc.getMegustas() );
        solicitud.put( "objectId", inc.getObjectId() );

        Backendless.Persistence.of( "Incidentes" ).save( solicitud, new AsyncCallback<Map>() {
            public void handleResponse(final Map savedIncidente ) {
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Gson gson = new Gson();
                String json = gson.toJson( savedIncidente );
                final Incidentes incidente = gson.fromJson(json, Incidentes.class);
                incidente.save();
                registrarLike();
            }

            public void handleFault( BackendlessFault fault ) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al registrar el me gusta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMegusta (final String incidenteID ) {
        loadProgreesUploadLoading();
        progressDialog.setMessage( mContext.getResources().getString(R.string.agergando_megusta ) );

        Backendless.Data.of( "Incidentes" ).findById( incidenteID, new AsyncCallback<Map>() {
            @Override
            public void handleResponse(final Map response) {
                Gson gson = new Gson();
                String json = gson.toJson( response );
                Incidentes incidente = gson.fromJson(json, Incidentes.class);
                int megustas = incidente.getMegustas();
                megustas++;
                inc.setMegustas( megustas );
                TextView incidenteMeGustas = (TextView) mView.findViewById(R.id.incidenteMeGustas);
                incidenteMeGustas.setText(  megustas + " me gustas" );
                ActualizarIncidenteLike();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(mContext, "Ocurrió un error al registrar el me gusta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openComentarios ( String incidenteID ) {
        ComentariosDialogFragment comentariosDialogFragment = ComentariosDialogFragment.newInstance( incidenteID );
        comentariosDialogFragment.show( fragmentManager, "dialog" );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pt) {
        final int position = holder.getAdapterPosition();
        inc = incidentes.get(position);
        if( inc.getUsuarioEmisor() != null ) {
            holder.incidenteNombreUsuario.setText(inc.getUsuarioEmisor().getName());
        }
        if( inc.getFecha() != null && !inc.getFecha().equals("") ){
            Locale LocaleBylanguageTag = Locale.forLanguageTag("en");
            TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

            DateFormat dfDateFull = DateFormat.getDateInstance(DateFormat.FULL);

            holder.incidenteFecha.setText(inc.getFecha());
            /*
            try {
                dfDateFull.parse( inc.getFecha() );
                String fecha = TimeAgo.using(dfDateFull.getCalendar().getTimeInMillis() , messages);
                holder.incidenteFecha.setText(fecha);
            } catch (ParseException e) {
                //e.printStackTrace();
            }
            */
            //long longDate = Long.parseLong(inc.getFecha() );
            //String fecha = TimeAgo.using(Long.parseLong(inc.getFecha()), messages);
            //holder.incidenteFecha.setText(fecha);
        }
        if( !inc.getDescripcion().equals("") ) {
            holder.incidenteContenido.setText(inc.getDescripcion());
        } else {
            holder.incidenteContenido.setVisibility(View.GONE);
        }
        if ( inc.getFoto() != null && !inc.getFoto().equals("") ) {
            Picasso.with(mContext).load( inc.getFoto() ).into( holder.incidenteImagen );
        } else {
            holder.incidenteImagen.setVisibility(View.GONE);
        }
        if ( inc.getAudio() != null && !inc.getAudio().equals("") ) {
            holder.linear_reproducir.setVisibility(View.VISIBLE);
        } else {
            holder.linear_reproducir.setVisibility(View.GONE);
        }
        holder.incidenteMeGustas.setText(  inc.getMegustas() + " me gustas" );
        holder.incidenteComentarios.setText(  inc.getComentarios() + " comentarios" );
        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        if( !inc.getUsuarioEmisor().getObjectId().equals( currentUserObjectId )  ) {
            holder.incidenteEditar.setVisibility(View.GONE);
        }
        holder.btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComentarios( inc.getObjectId() );
            }
        });
        holder.btnMeGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMegusta( inc.getObjectId() );
            }
        });
    }
}
