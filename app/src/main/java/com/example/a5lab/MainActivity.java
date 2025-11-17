package com.example.a5lab;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoader.DataLoaderListener {
    private ListView lvCurrencies;
    private EditText etFilter;
    private LinearLayout emptyView;
    private List<Currency> allCurrencies;
    private List<Currency> filteredCurrencies;
    private CurrencyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        System.out.println("MainActivity: onCreate started");

        initializeViews();
        setupListView();
        setupFilter();
        loadData();
    }

    private void initializeViews() {
        System.out.println("MainActivity: Initializing views");

        lvCurrencies = findViewById(R.id.lvCurrencies);
        etFilter = findViewById(R.id.etFilter);
        emptyView = findViewById(R.id.emptyView);

        allCurrencies = new ArrayList<>();
        filteredCurrencies = new ArrayList<>();
        adapter = new CurrencyAdapter(this, filteredCurrencies);
    }

    private void setupListView() {
        System.out.println("MainActivity: Setting up ListView");

        lvCurrencies.setAdapter(adapter);
        lvCurrencies.setEmptyView(emptyView);

        lvCurrencies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Currency currency = filteredCurrencies.get(position);
                String message = currency.getCode() + ": " + currency.getExchangeRate();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                System.out.println("MainActivity: Item clicked: " + message);
            }
        });
    }

    private void setupFilter() {
        System.out.println("MainActivity: Setting up filter");

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("MainActivity: beforeTextChanged - " + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("MainActivity: onTextChanged - " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();
                System.out.println("MainActivity: afterTextChanged - filtering with: " + filterText);
                filterCurrencies(filterText);
            }
        });
    }

    private void filterCurrencies(String filterText) {
        System.out.println("MainActivity: filterCurrencies called with: '" + filterText + "'");

        filteredCurrencies.clear();

        if (filterText.isEmpty()) {
            filteredCurrencies.addAll(allCurrencies);
            System.out.println("MainActivity: No filter applied, showing all " + allCurrencies.size() + " currencies");
        } else {
            String lowerCaseFilter = filterText.toLowerCase();
            for (Currency currency : allCurrencies) {
                if (currency.getCode().toLowerCase().contains(lowerCaseFilter)) {
                    filteredCurrencies.add(currency);
                }
            }
            System.out.println("MainActivity: Filter applied, showing " + filteredCurrencies.size() + " currencies");
        }

        adapter.notifyDataSetChanged();

        if (filteredCurrencies.isEmpty() && !allCurrencies.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lvCurrencies.setVisibility(View.GONE);
            System.out.println("MainActivity: No results, showing empty view");
        } else if (!filteredCurrencies.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            lvCurrencies.setVisibility(View.VISIBLE);
            System.out.println("MainActivity: Results found, showing list view");
        }
    }

    private void loadData() {
        System.out.println("MainActivity: loadData called");

        DataLoader dataLoader = new DataLoader(this);
        dataLoader.execute();

        emptyView.setVisibility(View.VISIBLE);
        lvCurrencies.setVisibility(View.GONE);
    }

    @Override
    public void onDataLoaded(InputStream inputStream) {
        System.out.println("MainActivity: onDataLoaded callback received");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("MainActivity: Starting XML parsing in background thread");
                    XmlParser parser = new XmlParser();
                    List<Currency> currencies = parser.parseXmlData(inputStream);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allCurrencies.clear();
                            allCurrencies.addAll(currencies);

                            String currentFilter = etFilter.getText().toString();
                            filterCurrencies(currentFilter);

                            System.out.println("MainActivity: UI updated with " + currencies.size() + " currencies");

                            if (currencies.isEmpty()) {
                                Toast.makeText(MainActivity.this, "No currency data loaded", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Loaded " + currencies.size() + " currencies", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    System.out.println("MainActivity: Error in background parsing - " + e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error processing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onError(Exception e) {
        System.out.println("MainActivity: onError callback received - " + e.getMessage());

        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Failed to load currency data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println("MainActivity: Data loading failed");

            emptyView.setVisibility(View.VISIBLE);
            lvCurrencies.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity: onDestroy called");
    }
}