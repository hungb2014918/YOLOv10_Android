package com.example.test3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test3.ml.BestFloat32;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.*;

import java.io.IOException;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultTextView; // Thêm TextView để hiển thị kết quả
    private final int INPUT_SIZE = 640;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultView); // Khởi tạo TextView

        // Nhận ảnh từ MainActivity
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            imageView.setImageBitmap(bitmap); // Hiển thị ảnh đã chọn

            // Thực hiện nhận diện đối tượng bằng mô hình tflite
            String results = detectObjects(bitmap); // Lưu kết quả nhận diện
            resultTextView.setText(results); // Hiển thị kết quả

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm thực hiện nhận diện đối tượng
    private String detectObjects(Bitmap bitmap) {
        StringBuilder resultStringBuilder = new StringBuilder(); // Dùng StringBuilder để xây dựng kết quả
        try {
            // Load mô hình TFLite
            BestFloat32 model = BestFloat32.newInstance(getApplicationContext());

            // Tạo input từ bitmap
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Chạy mô hình và nhận kết quả
            BestFloat32.Outputs outputs = model.process(image);
            List<Category> output = outputs.getOutputAsCategoryList();

            // Xử lý kết quả
            for (Category category : output) {
                float score = category.getScore(); // Lấy xác suất
                String label = category.getLabel(); // Lấy tên lớp

                if (score > 0.5) { // Chỉ hiển thị nếu xác suất lớn hơn 0.5
                    resultStringBuilder.append(label)
                            .append(": ")
                            .append(String.format("%.2f", score))
                            .append("\n"); // Thêm vào kết quả
                }
            }

            // Đóng mô hình khi không sử dụng nữa
            model.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultStringBuilder.toString(); // Trả về kết quả dưới dạng chuỗi
    }
}
