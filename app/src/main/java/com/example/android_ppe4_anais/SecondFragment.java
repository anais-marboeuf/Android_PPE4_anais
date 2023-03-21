package com.example.android_ppe4_anais;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.example.android_ppe4_anais.databinding.FragmentSecondBinding;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    // Variable qui permettent d'appeler le webservice
    private Async mThreadCon = null;
    private EditText login;
    private EditText mdp;
    private String url;
    private String[] mesparams;
    private EditText zoneIdentifiant, zoneMdp;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.fragment_second);
        //Récupération de la zone de saisie pour l'identifiant et le mot de passe
        zoneIdentifiant = (EditText) findViewById(R.id.etFragId);
        zoneMdp = (EditText) findViewById(R.id.etFragPassword);

        login = (EditText)view.findViewById(R.id.etFragId);
        mdp = (EditText)view.findViewById(R.id.etFragPassword);

        // Liaison entre ce fragment et le premier fragment avec le bouton cancel
        binding.bFragCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Naviguer de ce fragment jusqu'au premier fragment
                NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        // Liaison entre ce fragment et le troisieme fragment avec le bouton ok
        binding.bFragOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ((MainActivity)getActivity()).menuDeconnecte();


                //On renvoit à la mainActivity la gestion du login et du mdp via la function testMotDePasse
                ((MainActivity)getActivity()).testMotDePasse(login.getText().toString().trim(),mdp.getText().toString().trim());

                }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}