package com.misterchan.wisdomtreeclipboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String DO_HOMEWORK_URL_PREFIX = "https://onlineexamh5new.zhihuishu.com/stuExamWeb.html#/webExamList/dohomework/";
    private static final String STUDY_VIDEO_URL_PREFIX = "https://studyh5.zhihuishu.com/videoStudy.html#/";

    private static final String JS_ANSWERS = "" +
            "var answerNodeLabsToBeClicked = [], timer = {};" +
            "(function () {" +
            "    const CORRECT = [\"√\", \"对\", \"正确\"], INCORRECT = [\"×\", \"错\", \"错误\"];" +
            "    let number = %d, answer = \"%s\";" +
            "    let result = \"\";" +
            "    let questionSubject = document.getElementsByClassName(\"examPaper_subject mt20\")[number - 1];" +
            "    switch (questionSubject.getElementsByClassName(\"subject_type\")[0].children[0].textContent) {" +
            "        case \"【单选题】\":" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName(\"nodeLab\");" +
            "            answer = answer.replace(/\\s/g, \"\").replace(/。$/, \"\");" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName(\"label clearfix\")[0].children[2], examQuestionsAnswerTextContent = examQuestionsAnswer.textContent.replace(/\\s/g, \"\");" +
            "                result += answerNodeLabs[i].textContent;" +
            "                if (examQuestionsAnswerTextContent == answer || examQuestionsAnswerTextContent == \"√\" && CORRECT.includes(answer) || examQuestionsAnswerTextContent == \"×\" && INCORRECT.includes(answer)) {" +
            "                    answerNodeLabs[i].click();" +
            "                    result += \" ✓\";" +
            "                }" +
            "                result += \"\\n\";" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case \"【多选题】\":" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName(\"nodeLab\");" +
            "            answer = answer.replace(/\\s/g, \"\");" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName(\"label clearfix\")[0].children[1];" +
            "                result += answerNodeLabs[i].textContent;" +
            "                let b = answer.includes(examQuestionsAnswer.textContent.replace(/\\s/g, \"\"));" +
            "                if (b ^ examQuestionsAnswer.className == \"node_detail examquestions-answer fl onChecked\") {" +
            "                    answerNodeLabsToBeClicked.push(answerNodeLabs[i]);" +
            "                }" +
            "                if (b) {" +
            "                    result += \" ✓\";" +
            "                }" +
            "                result += \"\\n\";" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            timer = setInterval(function () {" +
            "                if (answerNodeLabsToBeClicked.length > 0) {" +
            "                    answerNodeLabsToBeClicked[0].click();" +
            "                    answerNodeLabsToBeClicked.shift();" +
            "                } else {" +
            "                    clearInterval(timer);" +
            "                }" +
            "            }, 500);" +
            "            break;" +
            "        case \"【判断题】\":" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName(\"nodeLab\");" +
            "            if (CORRECT.includes(answer)) {" +
            "                answerNodeLabs[0].click();" +
            "                result += \"对\";" +
            "            } else if (INCORRECT.includes(answer)) {" +
            "                answerNodeLabs[1].click();" +
            "                result += \"错\";" +
            "            }" +
            "            break;" +
            "        case \"【填空题】\":" +
            "            result += answer;" +
            "            break;" +
            "    }" +
            "    return result;" +
            "})()";

    private static final String JS_CLOSE_XUEQIANBIDU = "javascript:" +
            "var closingXueqianbiduTimer = setInterval(() => {" +
            "    let icons = document.getElementsByClassName(\"iconfont iconguanbi\");" +
            " alert('hi');" +
            "    if (icons.length > 0) {" +
            "        clearInterval(closingXueqianbiduTimer);" +
            "        icons[0].click();" +
            "    }" +
            "}, 1000)";

    private static final String JS_QUESTIONS = "" +
            "(function () {" +
            "    var questionSubjects = document.getElementsByClassName(\"examPaper_subject mt20\"), questions = [], answers = [];" +
            "    for (var i = 0; i < questionSubjects.length; ++i) {" +
            "        questions.push(questionSubjects[i].getElementsByClassName(\"subject_describe dynamic-fonts\")[0].textContent);" +
            "        var answer = \"\";" +
            "        switch (questionSubjects[i].getElementsByClassName(\"subject_type\")[0].children[0].textContent) {" +
            "            case \"【单选题】\":" +
            "                var answerNodeLab = questionSubjects[i].getElementsByClassName(\"nodeLab nodeLab12\"), answer = \"\";" +
            "                for (var j = 0; j < answerNodeLab.length; ++j) {" +
            "                    answer += answerNodeLab[j].textContent + \"\\n\";" +
            "                }" +
            "                answer = answer.slice(0, -1);" +
            "                break;" +
            "            case \"【多选题】\":" +
            "                var answerNodeLab = questionSubjects[i].getElementsByClassName(\"nodeLab\"), answer = \"\";" +
            "                for (var j = 0; j < answerNodeLab.length; ++j) {" +
            "                    answer += answerNodeLab[j].textContent + \"\\n\";" +
            "                }" +
            "                answer = answer.slice(0, -1);" +
            "                break;" +
            "            case \"【判断题】\":" +
            "                answer = \"对？错？\";" +
            "                break;" +
            "            case \"【填空题】\":" +
            "                answer = \" \";" +
            "                break;" +
            "        }" +
            "        answers.push(answer);" +
            "    }" +
            "    return questions.join(\",,\") + \",,,\" + answers.join(\",,\");" +
            "})()";

    private static final String JS_SET_SPEED_RATE = "javascript:" +
            "var speedTabs = document.getElementsByClassName(\"speedTab speedTab15\");" +
            "if (speedTabs.length > 0) {" +
            "    speedTabs[0].setAttribute(\"rate\", 15);" +
            "    speedTabs[0].click();" +
            "}";

    private static final String JS_ZOOM = "javascript:" +
            "document.getElementsByName(\"viewport\")[0].setAttribute(\"content\",\"width=device-width,initial-scale=0.25\")";

    private Button bAnswer;
    private ClipboardManager clipboardManager;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llControl, llQuestions;
    private ScrollView svQuestions;
    private String[] questions;
    private WebView webView;

    private final View.OnLongClickListener onQuestionsButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (clipboardManager.hasPrimaryClip()) {
                String text = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("前往");
                builder.setMessage(text);
                builder.setPositiveButton("确定", (dialog, which) -> webView.loadUrl(text));
                builder.show();
                return true;
            }
            return false;
        }
    };

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            if (url.startsWith(DO_HOMEWORK_URL_PREFIX)) {
                addMatchButton();
            } else if (url.startsWith(STUDY_VIDEO_URL_PREFIX)) {
                addVideoButton();
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
            if (!url.startsWith(DO_HOMEWORK_URL_PREFIX)) {
                llQuestions.removeAllViews();
                bAnswer.setEnabled(false);
                number = 0;
            }
            super.onPageStarted(view, url, favicon);
        }
    };

    private void addMatchButton() {
        llQuestions.removeAllViews();
        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("匹配题目");
        button.setOnClickListener(view -> {
            view.setEnabled(false);
            matchHomework();
        });
        llQuestions.addView(button);
    }

    private void addVideoButton() {
        llQuestions.removeAllViews();
        Button button;

        button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("缩放");
        button.setOnClickListener(view -> webView.loadUrl(JS_ZOOM));
        llQuestions.addView(button);

        button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("调速");
        button.setOnClickListener(view -> webView.loadUrl(JS_SET_SPEED_RATE));
        llQuestions.addView(button);
    }

    public void answer(View view) {
        if (clipboardManager.hasPrimaryClip()) {

            // Answer
            answer(number, clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());

            // Copy next question
            if (number < questions.length) {
                setNumber(number + 1);
                copyQuestion(number, questions[number - 1]);
                View v = llQuestions.getChildAt(number - 1);
                int y = v.getTop();
                if (svQuestions.getScrollY() + svQuestions.getHeight() < y + v.getHeight()) {
                    svQuestions.smoothScrollTo(0, y);
                }
            }
        }
    }

    /**
     * Answer a question.
     */
    private void answer(int number, String answer) {
        webView.evaluateJavascript(String.format(JS_ANSWERS, number, answer.replace("\n", "")), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                ((TextView) (llQuestions.getChildAt(number - 1)).findViewById(R.id.tv_answer)).setText(value.substring(1, value.length() - 1).replace("\\n", "\n"));
            }
        });
    }

    public void copyQuestion(View v) {
        setNumber((int) v.getTag());
        copyQuestion(number, ((TextView) v).getText().toString());
        bAnswer.setEnabled(true);
    }

    /**
     * Copy a question.
     */
    private void copyQuestion(int number, String question) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", question));
        Toast.makeText(MainActivity.this, "已复制第 " + number + " 题题目", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get all the questions from web.
     */
    private void matchHomework() {
        webView.evaluateJavascript(JS_QUESTIONS, value -> {
            String[] qAndA;
            if (!"null".equals(value) && (qAndA = value.substring(1, value.length() - 1).replace("\\n", "\n").split(",,,")).length == 2) {
                // qAndA[0] - all the questions.
                // qAndA[1] - all the answers.
                llQuestions.removeAllViews();
                questions = qAndA[0].split(",,");
                showQuestions(questions, qAndA[1].split(",,"));
            } else {
                runOnUiThread(this::addMatchButton);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAnswer = findViewById(R.id.b_answer);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        layoutInflater = LayoutInflater.from(this);
        llControl = findViewById(R.id.ll_control);
        llQuestions = findViewById(R.id.ll_questions);
        svQuestions = findViewById(R.id.sv_questions);

        findViewById(R.id.b_questions).setOnLongClickListener(onQuestionsButtonLongClickListener);

        webView = findViewById(R.id.wv);
        WebSettings ws = webView.getSettings();
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(false);
        ws.setBlockNetworkImage(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSupportMultipleWindows(false);
        ws.setSupportZoom(true);
        ws.setUseWideViewPort(true);
        ws.setUserAgentString("Chrome");
        webView.setWebViewClient(webViewClient);
        webView.requestFocusFromTouch();

        webView.loadUrl("https://onlineweb.zhihuishu.com/onlinestuh5");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            if (webView.getVisibility() == View.GONE) {
                llControl.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Set the current question number and change the text color.
     */
    private void setNumber(int number) {
        if (this.number > 0) {
            ((TextView) llQuestions.getChildAt(this.number - 1).findViewById(R.id.tv_question)).setTextColor(Color.BLACK);
        }
        this.number = number;
        ((TextView) llQuestions.getChildAt(this.number - 1).findViewById(R.id.tv_question)).setTextColor(Color.RED);
    }

    public void showHideQuestionsView(View v) {
        llControl.setVisibility(8 - llControl.getVisibility());
        webView.setVisibility(8 - webView.getVisibility());
    }

    /**
     * Show the questions.
     */
    private void showQuestions(String[] questions, String[] answers) {
        for (int i = 0; i < questions.length; i++) {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.question, null);

            // Question
            TextView tvQuestion = layout.findViewById(R.id.tv_question);
            int number = i + 1;
            tvQuestion.setText(number + " " + questions[i]);
            tvQuestion.setTag(number);

            // Answer
            ((TextView) layout.findViewById(R.id.tv_answer)).setText(answers[i]);

            llQuestions.addView(layout.findViewById(R.id.ll_question));
        }
    }
}