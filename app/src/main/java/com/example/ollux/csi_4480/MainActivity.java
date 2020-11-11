package com.example.ollux.csi_4480;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {
    private String fullName;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    private EditText editText;
    private ListView listView;
    private Button sendButton;

    private DatabaseReference databaseReference;
    private String stringMessage;

    private byte encryptionKey[] = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.listView);
        sendButton = findViewById(R.id.sendButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullName = value.getString("fname");
            }
        });

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Messages");

            try {
                cipher = Cipher.getInstance("AES");
                decipher = Cipher.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }

            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() >= 1) {

                        stringMessage = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                        stringMessage = stringMessage.substring(1, stringMessage.length() - 1);

                        String[] stringMessageArray = stringMessage.split(", ");
                        Arrays.sort(stringMessageArray);
                        String[] stringFinal = new String[stringMessageArray.length * 1];

                        try {
                            for (int i = 0; i < stringMessageArray.length; i++) {
                                String[] stringKeyValue = stringMessageArray[i].split("=", 2);
                                System.out.println(Arrays.toString(stringKeyValue));
                                String[] stringName = stringKeyValue[0].split(";");
                                stringFinal[1 * i] = (String) stringName[0] + ": " + AESDecryptionMethod(stringKeyValue[1]);
                            }

                            listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stringFinal));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                databaseReference.child(fullName + ";" + Long.toString(date.getTime())).setValue(AESEncryptionMethod(editText.getText().toString()));
                editText.setText("");
            }
        });
    }

    private String AESEncryptionMethod(String string){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}