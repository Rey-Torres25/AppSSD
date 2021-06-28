package fragmentsUsuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.ssd.appssd.MainActivity;
import com.ssd.appssd.MenuUsuario;
import com.ssd.appssd.R;

public class PerfilFragment extends Fragment {

    Button btnLogOut;
    FirebaseAuth mAuth;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        mAuth = FirebaseAuth.getInstance();
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

}