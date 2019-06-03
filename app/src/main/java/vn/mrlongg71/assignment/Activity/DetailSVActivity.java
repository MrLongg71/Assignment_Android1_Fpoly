package vn.mrlongg71.assignment.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import vn.mrlongg71.assignment.Adapter.DetailSVAdapter;
import vn.mrlongg71.assignment.Adapter.SVAdapter;
import vn.mrlongg71.assignment.Model.Students;
import vn.mrlongg71.assignment.R;

public class DetailSVActivity extends AppCompatActivity {

    ListView listSVDetail;
    ArrayList<Students> arr_DetailSv;

    DetailSVAdapter detailSVAdapter;
    EditText edtTenSVUpdate, edtNgaysinhSVupdate, edtTenlopSVUpdate, edtSDTSvUpdate, edtEmailSVUpdate, edtDiachiSVUpdate;
    Toolbar toolbar;
    int iduser, idclass,idsv;
    String sdt,email,place;
    final int REQUES_CODE_CALL = 123;
    final int REQUES_CODE_EMAIL = 456;
    final  int REQUES_CODE_MAP = 789;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sv);

        anhxa();
        listview_show();
        //back về
        Toolbar();
        //btnupdate
        callNow_email_Now_place_Now();


    }

    private void callNow_email_Now_place_Now() {
        listSVDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Students students = arr_DetailSv.get(position);
                final Dialog dialog = new Dialog(DetailSVActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_call_email_place);
                ImageView imgcallnow, imgemailnow,imgplacenow, imgupdate, imgdelete;
                imgcallnow = dialog.findViewById(R.id.callnow);
                imgemailnow = dialog.findViewById(R.id.emailnow);
                imgplacenow = dialog.findViewById(R.id.placenow);
                imgupdate = dialog.findViewById(R.id.update);
                imgdelete = dialog.findViewById(R.id.deleteSV);
                imgcallnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       sdt = students.getSdt();
                       if(sdt.length() == 0 || sdt.equals("0")){
                           Toast.makeText(DetailSVActivity.this, "Chưa có số diện thoại!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                       }else{
                           ActivityCompat.requestPermissions(DetailSVActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUES_CODE_CALL);
                            dialog.dismiss();
                       }



                        }
                });
                imgemailnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        email = students.getEmail();
                        if(email.length() == 0 || email.equals("null")){
                            Toast.makeText(DetailSVActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            ActivityCompat.requestPermissions(DetailSVActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUES_CODE_EMAIL);
                            dialog.dismiss();
                        }



                    }
                });
                imgplacenow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        place = students.getPlace();
                        if(place.equals("") || place.equals("null")){
                            Toast.makeText(DetailSVActivity.this, "Địa chỉ không hợp lệ!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            ActivityCompat.requestPermissions(DetailSVActivity.this, new String[]{Manifest.permission.INTERNET}, REQUES_CODE_MAP);
                            dialog.dismiss();
                            Toast.makeText(DetailSVActivity.this, "Chỉ đường " + place, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                imgupdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUpdateSV(students.getId(), students.getTenSV(),students.getDate(),students.getTenlop(),students.getSdt(),students.getEmail(),students.getPlace());
                        dialog.dismiss();
                    }
                });
                imgdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteSV();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


    }
    //check quyền truy cập call - email - place


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUES_CODE_CALL:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + sdt));
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Bạn chưa cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
                }


                break;
            case  REQUES_CODE_EMAIL:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    intent.putExtra(Intent.EXTRA_SUBJECT , "Tiêu đề");
                    intent.putExtra(Intent.EXTRA_TEXT, "Nội dung");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }else{
                    Toast.makeText(this, "Bạn chưa cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUES_CODE_MAP:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q="+place);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else{
                    Toast.makeText(this, "Bạn chưa cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
                }


                break;
        }




        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    //updateSV
    private void dialogUpdateSV(final int id, final String tenSV, String ngaysinhSV, String tenLop, String sdt, String email, String diachi) {

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_updatesv);
        Button btnHuyUpdateSv = dialog.findViewById(R.id.btnDialogHuyUpdateSV);
        Button btnUpdateSV = dialog.findViewById(R.id.btnDialogUpdateSV);

        edtTenSVUpdate = dialog.findViewById(R.id.edtTenSVUpdate);
        edtNgaysinhSVupdate = dialog.findViewById(R.id.edtNgaySinhSVUpdate);
        edtTenlopSVUpdate = dialog.findViewById(R.id.edtTenlopSVUpdate);
        edtSDTSvUpdate = dialog.findViewById(R.id.edtSDTSVUpdate);
        edtEmailSVUpdate = dialog.findViewById(R.id.edtEmailSVUpdate);
        edtDiachiSVUpdate = dialog.findViewById(R.id.edtDiaChiSVUpdate);
        edtTenSVUpdate.setText(tenSV);
        edtNgaysinhSVupdate.setText(ngaysinhSV);
        edtTenlopSVUpdate.setText(tenLop);
        edtSDTSvUpdate.setText(sdt);
        edtEmailSVUpdate.setText(email);
        edtDiachiSVUpdate.setText(diachi);

        btnHuyUpdateSv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnUpdateSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSVnew  = edtTenSVUpdate.getText().toString().trim();
                String ngaysinhSVnew = edtNgaysinhSVupdate.getText().toString().trim();
                String tenLopnew = edtTenlopSVUpdate.getText().toString().trim();
                String sdtnew = edtSDTSvUpdate.getText().toString().trim();
                String emailnew = edtEmailSVUpdate.getText().toString().trim();
                String diachinew = edtDiachiSVUpdate.getText().toString().trim();

                MainActivity.database.QueryData("UPDATE Students SET id = '"+id+"' ,tensv = '"+tenSVnew+"',ngaysinh = '"+ngaysinhSVnew+"', idclass = '"+idclass+"', iduser = '"+iduser+"', tenlop = '"+tenLopnew+"',sdt = '"+sdtnew+"', email = '"+emailnew+"', place = '"+diachinew+"' WHERE id = '"+idsv+"' AND idclass = '"+idclass+"' AND iduser = '"+iduser+"'");
                Toast.makeText(DetailSVActivity.this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                GetDataSV_Detail();
                detailSVAdapter.notifyDataSetChanged();
//                startActivity(new Intent(DetailSVActivity.this, SeeStudentsListActivity.class));
//
//                finish();
            }
        });
        dialog.show();



    }

    //xử lí xóa sv
    private void deleteSV() {
        final android.app.AlertDialog.Builder aBuilder = new AlertDialog.Builder(DetailSVActivity.this);
        aBuilder.setIcon(R.drawable.error);
        aBuilder.setTitle("Thông báo");
        aBuilder.setMessage("Bạn có muốn xóa Sinh viên " );

        aBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.database.QueryData("DELETE FROM Students WHERE id = '"+idsv+"' AND idclass = '"+idclass+"' AND iduser = '"+iduser+"'");
                Toast.makeText(getApplicationContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DetailSVActivity.this, HomeActivity.class));
                finishActivity(123);

            }
        });
        aBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        aBuilder.show();



    }

    private void listview_show() {
        arr_DetailSv = new ArrayList<>();
        detailSVAdapter = new DetailSVAdapter(getApplicationContext(), R.layout.custom_list_detail_sv, arr_DetailSv);
        listSVDetail.setAdapter(detailSVAdapter);
        GetDataSV_Detail();
    }
    private void GetDataSV_Detail() {
        Cursor dataSV = MainActivity.database.GetData("SELECT * FROM Students WHERE id = '"+idsv+"' AND idclass = '"+idclass+"' AND iduser = '"+iduser+"'");
        arr_DetailSv.clear();
        while (dataSV.moveToNext()){
            int id = dataSV.getInt(0);
            String tenSV = dataSV.getString(1);
            String date = dataSV.getString(2);
            int idclass = dataSV.getInt(3);
            int iduser = dataSV.getInt(4);
            String tenlop = dataSV.getString(5);
            String sdt = dataSV.getString(6);
            String email = dataSV.getString(7);
            String place = dataSV.getString(8);

            arr_DetailSv.add(new Students(id, tenSV, date , idclass, iduser, tenlop, sdt,email,place, dataSV.getBlob(9) ));
        }
        detailSVAdapter.notifyDataSetChanged();
    }
    private void Toolbar(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                GetDataSV_Detail();
            }
        });


    }



    private void anhxa() {

        listSVDetail = findViewById(R.id.list_DetailSV);
        toolbar = findViewById(R.id.toolbar_Detail);

        //nhận dữ liệu từ intent màn hình SeétudentsActivity

        Intent intent = getIntent();
        iduser = intent.getIntExtra("iduser" , -1);
        idclass = intent.getIntExtra("idclass" , -1);
        idsv = intent.getIntExtra("idsv" ,  -1);

    }
}
