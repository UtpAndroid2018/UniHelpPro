package pe.edu.utp.unihelppro.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.persistence.local.UserIdStorageFactory;

import org.w3c.dom.Text;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.fragments.AsignarIncidente;
import pe.edu.utp.unihelppro.fragments.CalificarIncidente;
import pe.edu.utp.unihelppro.fragments.ReportadosFragment;
import pe.edu.utp.unihelppro.fragments.CrearSolicitudFragment;
import pe.edu.utp.unihelppro.fragments.IncidentesFragment;
import pe.edu.utp.unihelppro.fragments.PreguntasFragment;
import pe.edu.utp.unihelppro.fragments.ReportarIncidente;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;
import pe.edu.utp.unihelppro.utils.Navigation;
import pe.edu.utp.unihelppro.utils.UserUtils;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    public NavigationView nvDrawer;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    Fragment fragment = null;
    boolean doubleBackToExitPressedOnce = false;
    private Handler mHandler;
    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        Connect.getInstance().setConnectActivity(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.nav_anadir_incidente);
                selectDrawerItem(menuItem);
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        //toggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);


        // Agregando opciones para cada incidente






        // Agregando el usuario actual al header
        View hView = nvDrawer.getHeaderView(0);
        TextView tvCurrentUser = (TextView) hView.findViewById(R.id.tvCurrentUser);
        TextView textView = (TextView) hView.findViewById(R.id.textView);

        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        UsuarioBackendless ub = new UsuarioBackendless( currentUserObjectId );
        ub.setupUser( null );
        tvCurrentUser.setText( ub.getName() );
        textView.setText( ub.getEmail() );


        //nvDrawer.setNavigationItemSelectedListener(this);
        setupDrawerContent(nvDrawer);

        MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.nav_incidentes);
        selectDrawerItem(menuItem);

        // OCULTAR EL MENU DESPEGABLE DE ACUERDO AL ROL

        if(Backendless.UserService.CurrentUser().getProperty("tipo").equals("cliente"))
        {
            Menu nav_Menu = nvDrawer.getMenu();
            nav_Menu.findItem(R.id.nav_usuarios_reportado).setVisible(false);

        }
        else
        {
            Menu nav_Menu = nvDrawer.getMenu();
            nav_Menu.findItem(R.id.nav_personal_reportado).setVisible(false);
            nav_Menu.findItem(R.id.nav_preguntas_frecuentes).setVisible(false);
            nav_Menu.findItem(R.id.nav_incidentes).setVisible(false);
        }

    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void gotoLogin() {
        Navigation.getInstance().startActivity( this, new Bundle(), getString(R.string.LoginActivityClassName), true);
    }

    private void gotoEstadisticas() {
        Navigation.getInstance().startActivity( this, new Bundle(), getString(R.string.EstadisticasActivityClassName), true);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_incidentes:
                fragmentClass = IncidentesFragment.class;
                break;
            case R.id.nav_anadir_incidente:
                fragmentClass = CrearSolicitudFragment.class;
                break;
            case R.id.nav_cerrar_sesion:
                UserUtils.logout();
                setTitle(menuItem.getTitle());
                mDrawer.closeDrawers();
                gotoLogin();
                return;
            case R.id.nav_estadisticas:
                gotoEstadisticas();
                return;
            case R.id.nav_preguntas_frecuentes:
                fragmentClass = PreguntasFragment.class;
                break;
            case R.id.nav_usuarios_reportado:
            case R.id.nav_personal_reportado:
                fragmentClass = ReportadosFragment.class;
                break;
            default:
                mDrawer.closeDrawers();
                return;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if( R.id.nav_anadir_incidente == menuItem.getItemId() ) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private Handler getHandler() {
        if (mHandler == null) {
            return new Handler(MainActivity.this.getMainLooper());
        }

        return mHandler;
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if ( fragment instanceof IncidentesFragment) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(mContext, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            } else {
                MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.nav_incidentes);
                selectDrawerItem(menuItem);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_calificar:
                CalificarIncidente calificarIncidente = CalificarIncidente.newInstance();
                calificarIncidente.show( getSupportFragmentManager() , "dialog" );
                return true;
            case R.id.action_reportar:
                ReportarIncidente reportarIncidente = ReportarIncidente.newInstance();
                reportarIncidente.show( getSupportFragmentManager() , "dialog" );
                return true;
            case R.id.action_asignar:
                AsignarIncidente asignarIncidente = AsignarIncidente.newInstance("", "");
                asignarIncidente.show( getSupportFragmentManager() , "dialog" );
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
