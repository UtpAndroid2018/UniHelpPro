package pe.edu.utp.unihelppro.adapters;

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

import static pe.edu.utp.unihelppro.Connect.getContext;

public class IncidenteAdapter extends RecyclerView.Adapter<IncidenteAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private List<Incidentes> incidentes;
    private final Context mContext;
    private int _position = -1;
    private static FragmentManager fragmentManager;

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
                ReportarIncidente reportarIncidente = ReportarIncidente.newInstance("", "");
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
        final Incidentes inc = incidentes.get(position);
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
