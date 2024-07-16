package com.abish.thesuitcase;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeFragment extends Fragment implements View.OnClickListener {
    TextView userEmail;
    Button logoutBtn;
    private FirebaseAuth mAuth;
    private View totalCardView;
    private View purchasedCardView;
    private View notPurchasedCardView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userEmail = view.findViewById(R.id.userEmail);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    public void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            userEmail.setText(email);
        }
        fetchAndDisplayCounts();
    }

    @Override
    public void onClick(View view) {
        if(view == logoutBtn){
            mAuth.signOut();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalCardView = view.findViewById(R.id.totalCardView);
        purchasedCardView = view.findViewById(R.id.purchasedCardView);
        notPurchasedCardView = view.findViewById(R.id.notPurchasedCardView);

        // Add card views to the layout


        fetchAndDisplayCounts();  // Move this call here or wherever appropriate in your code
    }
    private void fetchAndDisplayCounts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Replace "your_collection_name" with the actual name of your Firestore collection
            String collectionName = "SuitCase/" + user.getEmail() + "/Things";

            FirebaseFirestore.getInstance().collection(collectionName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int totalThingsCount = task.getResult().size();

                            // Initialize counts for purchased and not purchased items
                            int purchasedThingsCount = 0;
                            int notPurchasedThingsCount = 0;

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                // Assuming you have a field named "Purchased" in your Firestore documents
                                if (documentSnapshot.contains("Purchased") &&
                                        documentSnapshot.getBoolean("Purchased") != null) {
                                    if (documentSnapshot.getBoolean("Purchased")) {
                                        purchasedThingsCount++;
                                    } else {
                                        notPurchasedThingsCount++;
                                    }
                                }
                            }

                            // Set counts in respective card views
                            setCountInView(totalCardView, R.id.totalThingsCountTextView, totalThingsCount);
                            setCountInView(purchasedCardView, R.id.purchasedThingsCountTextView, purchasedThingsCount);
                            setCountInView(notPurchasedCardView, R.id.notPurchasedThingsCountTextView, notPurchasedThingsCount);
                        } else {
                            // Handle error
                        }
                    });
        }
    }

    private void setCountInView(View cardView, int textViewId, int count) {
        TextView countTextView = cardView.findViewById(textViewId);
        countTextView.setText(String.valueOf(count));
    }




}