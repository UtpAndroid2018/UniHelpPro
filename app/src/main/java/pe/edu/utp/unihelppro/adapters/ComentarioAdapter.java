package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;

import static pe.edu.utp.unihelppro.Connect.getContext;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {
    private List<Comentarios> comentarios;
    private final Context mContext;
    private int _position = -1;

    public ComentarioAdapter(List<Comentarios> comentariosList, Context mContext) {
        this.comentarios = comentariosList;
        this.mContext = mContext;
    }
    public void updateData(List<Comentarios> _comentarios) {
        comentarios.clear();
        comentarios.addAll( _comentarios );
        notifyDataSetChanged();
    }
    private void removeItem(int position) {
        comentarios.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_comentario, parent, false);
        return new ComentarioAdapter.ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return this.comentarios.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView comentarioNombreUsuario;
        private TextView comentarioFecha;
        private TextView comentarioContenido;
        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            comentarioNombreUsuario = (TextView) itemView.findViewById(R.id.comentarioNombreUsuario);
            comentarioFecha = (TextView) itemView.findViewById(R.id.comentarioFecha);
            comentarioContenido = (TextView) itemView.findViewById(R.id.comentarioContenido);

            //dividerProject= (ImageView) itemView.findViewById(R.id.dividerProject);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int pt) {
        final int position = holder.getAdapterPosition();

        holder.comentarioNombreUsuario.setText(comentarios.get(position).getUsuario().getProperties().getName());
        //holder.comentarioFecha.setText(comentarios.get(position).getFecha());

        holder.comentarioFecha.setText(comentarios.get(position).getTimeAgo());

        holder.comentarioContenido.setText(comentarios.get(position).getDescripcion());

        if ( position == this.comentarios.size() - 1 ){
            //holder.dividerProject.setVisibility(View.GONE);
        }
    }
}
