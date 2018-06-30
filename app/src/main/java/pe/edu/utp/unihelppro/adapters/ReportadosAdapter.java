package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.PreguntasFrecuentes;
import pe.edu.utp.unihelppro.models.Reportados;


public class ReportadosAdapter extends RecyclerView.Adapter<ReportadosAdapter.ViewHolder> {

    private List<Reportados> mValues;
    private OnReportadosListener mListener;
    private Context mContext;


    public interface OnReportadosListener {
        void onReportadoInteraction(Reportados item);
    }


    public ReportadosAdapter(List<Reportados> items, Context context, OnReportadosListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_reportado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nombreUsuario.setText(mValues.get(position).getUsuarioReceptor().getProperties().getName());
        //holder.categoriaUsuario.setText(mValues.get(position).getUsuarioReceptor().getProperties().getName());

        holder.rbPuntuacion.setRating( mValues.get(position).getCalificacion() );
        holder.fecha.setText( mValues.get(position).getTimeAgo() );
        holder.rbPuntuacion.setIsIndicator( true );
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onReportadoInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        Reportados mItem;
        TextView nombreUsuario;
        TextView categoriaUsuario;
        TextView fecha;
        RatingBar rbPuntuacion;

        ViewHolder(View view) {
            super(view);
            mView = view;
            nombreUsuario = (TextView) view.findViewById(R.id.nombreUsuario);
            categoriaUsuario = (TextView) view.findViewById(R.id.categoriaUsuario);
            fecha = (TextView) view.findViewById(R.id.fecha);
            rbPuntuacion = (RatingBar) view.findViewById(R.id.rbPuntuacion);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombreUsuario.getText() + "'";
        }
    }
}
