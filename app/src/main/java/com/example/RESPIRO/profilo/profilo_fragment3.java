package com.example.RESPIRO.profilo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.RESPIRO.DB.UserDB;
import com.example.RESPIRO.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profilo_fragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profilo_fragment3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout classificaLayout;
    private LinearLayout posClassificaUtente;

    public profilo_fragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profilo_fragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static profilo_fragment3 newInstance(String param1, String param2) {
        profilo_fragment3 fragment = new profilo_fragment3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment3_profilo, container, false);
        classificaLayout = v.findViewById(R.id.classifica);
        posClassificaUtente = v.findViewById(R.id.posClassificaUtente);

        UserDB.setClassifica(getContext(), classificaLayout, posClassificaUtente);
        return v;
    }
}