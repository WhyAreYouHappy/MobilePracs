package com.mirea.kuznetsovkv.employeedb;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Тег для логирования
    private static final String TAG = "EmployeeDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = App.getInstance().getDatabase();
        EmployeeDao employeeDao = db.employeeDao();

        //Добавление сотрудника
        Employee employee = new Employee();
        employee.name = "Саня Санев";      // Имя сотрудника
        employee.salary = 7777777;         // Зарплата
        employeeDao.insert(employee);      // Вставка в БД

        //Чтение сотрудников
        List<Employee> employees = employeeDao.getAll();
        for (Employee e : employees) {
            // Логируем информацию о каждом сотруднике
            Log.d(TAG, e.id + ": " + e.name + ", " + e.salary);
        }

        //Обновление зп первого сотрудника
        if (!employees.isEmpty()) {
            Employee first = employees.get(0);
            first.salary += 50000;
            employeeDao.update(first);
        }
    }
}