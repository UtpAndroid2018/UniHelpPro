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
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.Reportados;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;
import pe.edu.utp.unihelppro.models.UsuarioProperties;

import static pe.edu.utp.unihelppro.Connect.getContext;

public class IncidenteAdapter extends RecyclerView.Adapter<IncidenteAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener, ReportarIncidente.OnReportarListener {
    private List<Incidentes> incidentes;
    private final Context mContext;
    private int _position = -1;
    private static FragmentManager fragmentManager;
    private ProgressDialog progressDialog;
    private Incidentes inc;

    public IncidenteAdapter(List<Incidentes> incidentesList, Context mContext) {
        this.incidentes = incidentesList;
        this.mContext = mContext;
        this.fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
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

    private void setRelations(final BackendlessUser currentUser, final Map mapData, String tablaName, String relation, final Map mapSavedReporte, final Reportados savedReporte, final boolean _final ) {
        IDataStore<Map> contactStorage = Backendless.Data.of( tablaName );
        List<Map> addresses = new ArrayList<Map>();
        addresses.add( mapData );
        contactStorage.setRelation(mapSavedReporte, relation, addresses, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {
                Gson gson = new Gson();
                if( _final ) {
                    Incidentes incidente = new Incidentes( inc.getObjectId() );
                    incidente.getData();
                    savedReporte.setIncidente( incidente );
                    savedReporte.setUsuarioEmisor( setupUserMap( currentUser ) );
                    savedReporte.save();
                    //comentariosList.add( 0, comentario );
                    dismmisProgreesLoading();
                    Toast.makeText(mContext, "Incidente reportado", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, String> solicitud = new HashMap<>();
                    solicitud.put( "objectId", inc.getObjectId() );
                    setRelations( currentUser, solicitud,"UsuariosReportados" , "incidente:UsuariosReportados:1", mapSavedReporte, savedReporte, true );
                }
                //dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dismmisProgreesLoading();
                Toast.makeText(mContext, "Ocurrió un error al reportar el inicidente", Toast.LENGTH_SHORT).show();
                //enviar_comentario.setEnabled(true);
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
                ObjectMapper mapper = new ObjectMapper();
                final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Gson gson = new Gson();
                String json = gson.toJson( savedReporte );
                final Reportados reportado = gson.fromJson(json, Reportados.class);
                reportado.save();
                if ( currentUser != null ) {
                    Map userMap = currentUser.getProperties();
                    setRelations( currentUser, userMap,"UsuariosReportados" , "usuarioEmisor:UsuariosReportados:1", savedReporte, reportado, false );
                } else {
                    Backendless.Data.of( BackendlessUser.class ).findById( currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(final BackendlessUser response) {
                            Map userMap = response.getProperties();
                            setRelations( response, userMap,"UsuariosReportados" , "usuarioEmisor:UsuariosReportados:1", savedReporte, reportado, false );
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(mContext, "Ocurrió un error al publicar el comentario", Toast.LENGTH_SHORT).show();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView incidenteNombreUsuario;
        private TextView incidenteFecha;
        private TextView incidenteContenido;
        private ImageView incidenteImagen;
        private ImageView incidenteEditar;
        private ImageView incidenteBoton;
        private RecyclerView recycler_comentarios_incidente;

        private Button btnMeGusta;
        private Button btnComentar;
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            incidenteNombreUsuario = (TextView) itemView.findViewById(R.id.incidenteNombreUsuario);
            incidenteFecha = (TextView) itemView.findViewById(R.id.incidenteFecha);
            incidenteContenido = (TextView) itemView.findViewById(R.id.incidenteContenido);
            incidenteImagen = (ImageView) itemView.findViewById(R.id.incidenteImagen);

            incidenteEditar = (ImageView) itemView.findViewById(R.id.incidenteEditar);
            incidenteBoton = (ImageView) itemView.findViewById(R.id.incidenteBoton);

            btnMeGusta = (Button) itemView.findViewById(R.id.btnMeGusta);
            btnComentar = (Button) itemView.findViewById(R.id.btnComentar);

            //dividerProject= (ImageView) itemView.findViewById(R.id.dividerProject);

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
                CalificarIncidente calificarIncidente = CalificarIncidente.newInstance("", "");
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


        Locale LocaleBylanguageTag = Locale.forLanguageTag("es");
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();
        String fecha = TimeAgo.using(Long.parseLong(inc.getFecha()), messages);
        holder.incidenteFecha.setText(fecha);
        if( !inc.getDescripcion().equals("") ) {
            holder.incidenteContenido.setText(inc.getDescripcion());
        }
        if ( inc.getFoto() != null && !inc.getFoto().equals("") ) {
            Picasso.with(mContext).load( inc.getFoto() ).into( holder.incidenteImagen );
        } else {
            holder.incidenteImagen.setVisibility(View.GONE);
        }
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
    }
}
