package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.models.Reportados;


public class ReportarIncidente extends DialogFragment {
    
    private OnReportarListener mListener;
    private EditText comentario_edittext;
    private RatingBar rbPuntuacion;

    public ReportarIncidente() {
        
    }

    public static ReportarIncidente newInstance() {
        ReportarIncidente fragment = new ReportarIncidente();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FixedDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportar_incidente, container, false);
        comentario_edittext = (EditText) view.findViewById(R.id.comentario_edittext);
        Button accion_eviar = (Button) view.findViewById(R.id.accion_eviar);
        rbPuntuacion = (RatingBar) view.findViewById(R.id.rbPuntuacion);
        accion_eviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });
        return view;
    }

    
    public void onButtonPressed() {
        if( rbPuntuacion.getRating() < 0.5 ){
            Toast.makeText( getContext() , getContext().getResources().getText( R.string.error_seleccionar_calificacion ), Toast.LENGTH_SHORT).show();
            return;
        }
        if( comentario_edittext.getText().toString().equals("") ){
            Toast.makeText( getContext() , getContext().getResources().getText( R.string.ingresar_texto ), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mListener != null) {
            Reportados reporte = new Reportados();
            reporte.setDescripcion( comentario_edittext.getText().toString() );
            reporte.setCalificacion( rbPuntuacion.getRating() );
            mListener.onReportar( reporte );
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReportarListener) {
            mListener = (OnReportarListener) context;
        } else {
            
        }
    }

    public OnReportarListener getmListener() {
        return mListener;
    }

    public void setmListener(OnReportarListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    
    public interface OnReportarListener {
        void onReportar(Reportados reporte);
    }
}
