package neu.madcourse.walkwithme.userlog;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import neu.madcourse.walkwithme.MainActivity;
import neu.madcourse.walkwithme.R;

public class LogoutFragment extends Fragment{
//    Button yes;
//    Button no;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        yes = (Button)view.findViewById(R.id.yes_logout);
//        no = (Button)view.findViewById(R.id.not_logout);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.yes_logout:
//                Intent login = new Intent(getActivity(), LoginActivity.class);
//                startActivity(login);
//                break;
//            case R.id.not_logout:
//                Intent main = new Intent(getActivity(), MainActivity.class);
//                startActivity(main);
//                break;
//        }
//    }
}