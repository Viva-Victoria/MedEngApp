package ru.medeng.mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.medeng.mobile.api.Api;
import ru.medeng.mobile.ui.basic.NavigationUpdateListener;
import ru.medeng.models.user.AccessLevel;

public class MainActivity extends AppCompatActivity {
    public static NavigationUpdateListener navigationUpdateListener;
    private SharedPreferences apiPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiPreferences = getSharedPreferences("API", MODE_PRIVATE);

        if (!connectToApi()) {
            return;
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navigationUpdateListener = () -> {
            AccessLevel level = Api.getInstance().getAuth().getAccessLevel();
            switch(level) {
                case Guest:
                    navController.setGraph(R.navigation.guest_navigation);
                    navView.getMenu().getItem(1).setTitle(R.string.account_title);
                    break;
                case Customer:
                    navController.setGraph(R.navigation.customer_navigation);
                    navView.getMenu().getItem(1).setTitle(R.string.account_title);
                    break;
                case Operator:
                    navController.setGraph(R.navigation.operator_navigation);
                    navView.getMenu().getItem(1).setTitle(R.string.customers_title);
                    break;
                case Storekeeper:
                    navController.setGraph(R.navigation.storekeeper_navigation);
            }

            navView.getMenu().getItem(2).setVisible(Api.getInstance().getAuth().getCachedAccessLevel() != AccessLevel.Guest);
            navView.invalidate();
        };
        navigationUpdateListener.invalidateNavigationBar();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_account, R.id.navigation_list)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onPause() {
        super.onPause();

        disconnectFromApi();
    }

    private boolean connectToApi() {
        Api api = Api.newInstance();
        if (!api.ping()) {
            Toast.makeText(getApplicationContext(), "Сервис временно недоступен. Проверьте подключение к сети", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }


        api.read(apiPreferences);
        return true;
    }

    private void disconnectFromApi() {
        Api.getInstance().save(apiPreferences);
    }

}