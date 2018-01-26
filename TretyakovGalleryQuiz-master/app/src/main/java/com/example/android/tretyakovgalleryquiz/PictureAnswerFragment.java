package com.example.android.tretyakovgalleryquiz;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */

public class PictureAnswerFragment extends Fragment implements View.OnClickListener {
    private PictureAnswerListener listener;
    private PictureAnswer pictureAnswer;

    public PictureAnswerFragment() {
        // Required empty public constructor
    }

    public void onClick(View v) {
        if (listener != null) {
            // Сообщить слушателю о том, что на одной из кнопок был сделан щелчок
            listener.onButtonClicked(v.getId(), pictureAnswer.getCorrectAnswer());
        }
    }

    interface PictureAnswerListener {
        void onButtonClicked(long id, String correctAnswer);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_answer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeFragment();
    }

    private void initializeFragment() {
        View view = getView();

        if (view != null) {
            String[] answers = pictureAnswer.getAnswers();

            ImageView pictureImageView = view.findViewById(R.id.picture_image_view);
            pictureImageView.setImageResource(pictureAnswer.getPictureId());

            TextView questionTextView = view.findViewById(R.id.question_text_view);
            questionTextView.setText(pictureAnswer.getQuestion());

            // Получение ссылок на кнопки
            Button answer1Button = view.findViewById(R.id.answer_1_button);
            Button answer2Button = view.findViewById(R.id.answer_2_button);
            Button answer3Button = view.findViewById(R.id.answer_3_button);
            Button answer4Button = view.findViewById(R.id.answer_4_button);

            // Назначение слушателей и текста кнопкам
            answer1Button.setOnClickListener(this);
            answer1Button.setText(answers[0]);
            answer2Button.setOnClickListener(this);
            answer2Button.setText(answers[1]);
            answer3Button.setOnClickListener(this);
            answer3Button.setText(answers[2]);
            answer4Button.setOnClickListener(this);
            answer4Button.setText(answers[3]);
        }
    }

    // Вызывается при присоединении фрагмента к активности
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.listener = (PictureAnswerListener) context;
    }

    // Определяются данные для отображения
    public void setAnswerData(int pictureId) {
        pictureAnswer = PictureAnswer.PICTURE_ANSWERS_DATA[pictureId];
    }

}
