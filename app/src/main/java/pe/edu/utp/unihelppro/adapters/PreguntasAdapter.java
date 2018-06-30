package pe.edu.utp.unihelppro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.PreguntasFrecuentes;


public class PreguntasAdapter extends RecyclerView.Adapter<PreguntasAdapter.ViewHolder> {

    private List<PreguntasFrecuentes> mValues;
    private OnPreguntasListener mListener;
    private Context mContext;


    public interface OnPreguntasListener {
        void onPreguntaInteraction(PreguntasFrecuentes item);
    }

    public PreguntasAdapter(List<PreguntasFrecuentes> items, Context context, OnPreguntasListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pregunta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText( "" + ( position + 1 ) );
        holder.mTitle.setText(mValues.get(position).getTitulo());
        holder.mContentView.setText(mValues.get(position).getDescripcion());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onPreguntaInteraction(holder.mItem);
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
        final TextView mIdView;
        final TextView mContentView;
        final TextView mTitle;
        PreguntasFrecuentes mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mTitle = (TextView) view.findViewById(R.id.item_title);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
