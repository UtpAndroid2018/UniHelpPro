package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        private TextView mIdView;
        private TextView mContentView;
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mIdView = (TextView) itemView.findViewById(R.id.item_number);
            mContentView = (TextView) itemView.findViewById(R.id.content);
            //lbl_name = (TextView) itemView.findViewById(R.id.lbl_name);
            //lbl_representante = (TextView) itemView.findViewById(R.id.lbl_representante);
            //lbl_proyectos = (TextView) itemView.findViewById(R.id.lbl_proyectos);
            //lbl_fecha = (TextView) itemView.findViewById(R.id.lbl_fecha);
            //dividerProject= (ImageView) itemView.findViewById(R.id.dividerProject);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int pt) {
        final int position = holder.getAdapterPosition();

        holder.mIdView.setText(incidentes.get(position).getEstado());
        holder.mContentView.setText(incidentes.get(position).getDescripcion());
        if ( position == this.incidentes.size() - 1 ){
            //holder.dividerProject.setVisibility(View.GONE);
        }
    }
}
