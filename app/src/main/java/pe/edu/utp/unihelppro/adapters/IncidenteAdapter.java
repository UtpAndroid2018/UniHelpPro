package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.Incidentes;

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
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            incidenteNombreUsuario = (TextView) itemView.findViewById(R.id.incidenteNombreUsuario);
            incidenteFecha = (TextView) itemView.findViewById(R.id.incidenteFecha);
            incidenteContenido = (TextView) itemView.findViewById(R.id.incidenteContenido);
            incidenteImagen = (ImageView) itemView.findViewById(R.id.incidenteImagen);

            //dividerProject= (ImageView) itemView.findViewById(R.id.dividerProject);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int pt) {
        final int position = holder.getAdapterPosition();

        holder.incidenteContenido.setText(incidentes.get(position).getDescripcion());
        Picasso.with(mContext).load( incidentes.get(position).getFoto() ).into( holder.incidenteImagen );

        if ( position == this.incidentes.size() - 1 ){
            //holder.dividerProject.setVisibility(View.GONE);
        }
    }
}
