package com.bgarlewicz.guessthenumber;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuessTheNumberActivity extends AppCompatActivity {

    public static final String ARGUMENT_NUMBER = "ARGUMENT_NUMBER";
    public static final String EXTRA_MESSAGE = "com.bgarlewicz.guessthenumber.EXTRA_MESSAGE";
    public static final String EXTRA_USER_GUESS = "com.bgarlewicz.guessthenumber.EXTRA_USER_GUESS";
    public static final String EXTRA_HIDDEN_NUMBER = "com.bgarlewicz.guessthenumber.EXTRA_HIDDEN_NUMBER";
    public static final String EXTRA_GUESS_CORRECT = "com.bgarlewicz.guessthenumber.EXTRA_GUESS_CORRECT";

    private int mNumberToGuess;
    private boolean mIsGuessCorrect;
    private InputMethodManager mInputManager;
    private String mMessage;
    private String mUserGuessText;

    @BindView(R.id.btn_guess) Button btnGuess;
    @BindView(R.id.et_guess) EditText etGuess;
    @BindView(R.id.tv_info) TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_number);
        ButterKnife.bind(this);
        mInputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (savedInstanceState != null){
            mNumberToGuess = savedInstanceState.getInt(EXTRA_HIDDEN_NUMBER);
            mIsGuessCorrect = savedInstanceState.getBoolean(EXTRA_GUESS_CORRECT);
            mUserGuessText = savedInstanceState.getString(EXTRA_USER_GUESS);
            etGuess.setText(mUserGuessText);
            mMessage = savedInstanceState.getString(EXTRA_MESSAGE);
            tvInfo.setText(mMessage);
            setTextOnGuessButton();
        } else {
            startNewGame();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_MESSAGE, mMessage);
        outState.putString(EXTRA_USER_GUESS, mUserGuessText);
        outState.putBoolean(EXTRA_GUESS_CORRECT, mIsGuessCorrect);
        outState.putInt(EXTRA_HIDDEN_NUMBER, mNumberToGuess);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.btn_guess)
    public void clickButton() {
        if (mIsGuessCorrect) {
            startNewGame();
        } else {
            checkGuess();
        }
    }

    private void checkGuess() {
        mMessage = getString(R.string.error_invalid_number);
        mUserGuessText = etGuess.getText().toString();
        try {
            int userNumber = Integer.parseInt(mUserGuessText);
            if (userNumber < mNumberToGuess) {
                mMessage = userNumber + " " + getString(R.string.msg_number_low);
            } else if (userNumber > mNumberToGuess) {
                mMessage = userNumber + " " + getString(R.string.msg_number_high);
            } else {
                mMessage = userNumber + " " + getString(R.string.msg_number_right);
                showHideKeyboard(etGuess);
                showHiddenNumber(mNumberToGuess);
                mIsGuessCorrect = true;
                setTextOnGuessButton();
            }
        } catch (NumberFormatException e) {
        } finally {
            tvInfo.setText(mMessage);
            if (!mIsGuessCorrect) {
                etGuess.requestFocus();
                etGuess.selectAll();
            }
        }
    }

    private void showHiddenNumber(int hiddenNumber) {
        NumberFragment numberFragment = new NumberFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_NUMBER, String.valueOf(hiddenNumber));
        numberFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.container, numberFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startNewGame(){
        generateNewNumber();
        tvInfo.setText(R.string.msg_game_info);
        etGuess.setText("");
        setTextOnGuessButton();
        showHideKeyboard(etGuess);
        if (mIsGuessCorrect){
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                    .replace(R.id.container, new ImageFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ImageFragment())
                    .commit();
        }
    }

    private void generateNewNumber(){
        mNumberToGuess = new Random().nextInt(100)+1;
        mIsGuessCorrect = false;
    }

    private void showHideKeyboard(EditText editText){
        if (mIsGuessCorrect){
            editText.clearFocus();
            mInputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } else {
            editText.requestFocus();
            mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void setTextOnGuessButton(){
        if (mIsGuessCorrect){
            btnGuess.setText(R.string.btn_text_new_game);
        } else {
            btnGuess.setText(R.string.btn_text_guess);
        }
    }

    public static class NumberFragment extends android.app.Fragment{
        public NumberFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_number, container, false);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (getArguments() != null) {
                String text = getArguments().getString(ARGUMENT_NUMBER, "");
                TextView answerView = (TextView) getView().findViewById(R.id.tv_answer);
                answerView.setText(text);
            }
        }
    }

    public static class ImageFragment extends android.app.Fragment{
        public ImageFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_image, container, false);
        }
    }

}
