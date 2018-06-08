package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pe.edu.utp.unihelppro.R;


public class SelectImageDialogFragment extends DialogFragment {

    
    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;

    
    public static SelectImageDialogFragment newInstance(int itemCount) {
        final SelectImageDialogFragment fragment = new SelectImageDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_itemimage_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemImageAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onItemImageClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            
            super(inflater.inflate(R.layout.fragment_itemimage_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemImageClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });
        }

    }

    private class ItemImageAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;

        ItemImageAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch ( position ){
                case 0:
                    holder.text.setText(getResources().getText(R.string.seleccionar_foto));
                    break;
                case 1:
                    holder.text.setText(getResources().getText(R.string.tomar_foto));
                    break;
                default:
                    holder.text.setText(getResources().getText(R.string.sin_opcion) );
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
