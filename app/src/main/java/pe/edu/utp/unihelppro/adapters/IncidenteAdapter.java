package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;

import static pe.edu.utp.unihelppro.Connect.getContext;

public class IncidenteAdapter extends RecyclerView.Adapter<IncidenteAdapter.ViewHolder> {
    private List<Incidentes> incidentes;
    private final Context mContext;
    private int _position = -1;

    public IncidenteAdapter(List<Incidentes> incidentesList, Context mContext) {
        this.incidentes = incidentesList;
        this.mContext = mContext;
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


    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView incidenteNombreUsuario;
        private TextView incidenteFecha;
        private TextView incidenteContenido;
        private ImageView incidenteImagen;
        private ImageView incidenteEditar;
        private RecyclerView recycler_comentarios_incidente;
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            incidenteNombreUsuario = (TextView) itemView.findViewById(R.id.incidenteNombreUsuario);
            incidenteFecha = (TextView) itemView.findViewById(R.id.incidenteFecha);
            incidenteContenido = (TextView) itemView.findViewById(R.id.incidenteContenido);
            incidenteImagen = (ImageView) itemView.findViewById(R.id.incidenteImagen);
            incidenteEditar = (ImageView) itemView.findViewById(R.id.incidenteEditar);
            recycler_comentarios_incidente = (RecyclerView) itemView.findViewById(R.id.recycler_comentarios_incidente);

            //dividerProject= (ImageView) itemView.findViewById(R.id.dividerProject);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int pt) {
        final int position = holder.getAdapterPosition();
        Incidentes inc = incidentes.get(position);
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

        /*
        IDataStore<Map> incidentesStorage = Backendless.Data.of( "Comentarios" );
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "incidente='"+ incidentes.get(position).getObjectId() +"'" );
        queryBuilder.setSortBy( "-fecha" );
        queryBuilder.setPageSize( 2 );
        incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse( List<Map> maps ) {
                List<Comentarios> comentariosList = new ArrayList<>();
                for ( Map map: maps) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson( map );
                        Comentarios comentario = gson.fromJson(json, Comentarios.class);
                        comentario.save();
                        comentariosList.add( comentario );

                    } catch ( IllegalArgumentException exception ) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                holder.recycler_comentarios_incidente.setHasFixedSize(true);
                LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
                //incidentesLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                holder.recycler_comentarios_incidente.setLayoutManager(incidentesLayoutManager);

                ComentarioAdapter incidentesAdapter = new ComentarioAdapter(comentariosList, mContext );
                holder.recycler_comentarios_incidente.setAdapter(incidentesAdapter);
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(getContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );
        */
    }
}
