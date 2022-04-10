package com.misterchan.wisdomtreeclipboard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private static final String URL_PREFIX_DO_HOMEWORK = "https://onlineexamh5new.zhihuishu.com/stuExamWeb.html#/webExamList/dohomework/";
    private static final String URL_PREFIX_STUDY_VIDEO = "https://studyh5.zhihuishu.com/videoStudy.html#/";

    private static final String JS_ANSWERS = "" +
            "var answerNodeLabsToBeClicked = [], timer = {};" +
            "(() => {" +
            "    const CORRECT = [\"√\", \"对\", \"正确\"], INCORRECT = [\"×\", \"错\", \"错误\"];" +
            "    let number = %d, answer = \"%s\";" +
            "    let result = \"\";" +
            "    let questionSubject = document.getElementsByClassName(\"examPaper_subject mt20\")[number - 1];" +
            "    switch (questionSubject.getElementsByClassName(\"subject_type\")[0].children[0].textContent) {" +
            "        case \"【单选题】\":" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName(\"nodeLab\");" +
            "            answer = answer.replace(/\\s/g, \"\").replace(/[;。；]$/, \"\");" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName(\"label clearfix\")[0].children[2], examQuestionsAnswerTextContent = examQuestionsAnswer.textContent.replace(/\\s/g, \"\").replace(/[;。；]$/, \"\");" +
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
            "                let b = answer.includes(examQuestionsAnswer.textContent.replace(/\\s/g, \"\").replace(/[;。；]$/, \"\"));" +
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

    private static final String JS_AUTO_PLAY = "javascript:" +
            "var isPlaying = false;" +
            "setInterval(() => {" +
            "" +
            "    let dialog = document.getElementsByClassName(\"el-dialog\")[5];" +
            "    if (dialog != undefined && dialog.getAttribute(\"aria-label\") == \"弹题测验\") {" +
            "        dialog.getElementsByClassName(\"topic-item\")[0].click();" +
            "        dialog.getElementsByClassName(\"el-dialog__headerbtn\")[0].click();" +
            "        document.getElementById(\"vjs_container_html5_api\").play();" +
            "    }" +
            "" +
            "    let defini = document.getElementsByClassName(\"definiBox\")[0];" +
            "    if (defini != undefined && defini.firstChild.textContent != \"流畅\") {" +
            "        defini.getElementsByClassName(\"line1bq switchLine\")[0].click();" +
            "    }" +
            "" +
            "    if (document.getElementsByClassName(\"speedBox\")[0].textContent != \"X 1.5X 1.5X 1.25X 1.0\") {" +
            "        let speedTab = document.getElementsByClassName(\"speedTab speedTab15\")[0];" +
            "        speedTab.setAttribute(\"rate\", 15);" +
            "        speedTab.click();" +
            "    }" +
            "" +
            "    let currentPlay = document.getElementsByClassName(\"clearfix video current_play\")[0];" +
            "    if (currentPlay.getElementsByClassName(\"fl time_icofinish\").length > 0) {" +
            "        if (isPlaying) {" +
            "            isPlaying = false;" +
            "            document.getElementById(\"nextBtn\").click();" +
            "        }" +
            "    } else {" +
            "        isPlaying = true;" +
            "    }" +
            "" +
            "    if (document.getElementById(\"playButton\").getAttribute(\"class\") == \"playButton\") {" +
            "        document.getElementById(\"vjs_container_html5_api\").play();" +
            "    }" +
            "" +
            "}, 1000);";

    private static final String JS_QUESTIONS = "" +
            "(() => {" +
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

    private static final String JS_ZOOM = "javascript:" +
            "document.getElementsByName(\"viewport\")[0].setAttribute(\"content\",\"width=device-width,initial-scale=0.25\")";

    private Button bAnswer, bMatch, bQuestions;
    private ClipboardManager clipboardManager;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llQuestions, llWork, llWebView;
    private ScrollView svQuestions;
    private String[] questions;
    private WebView webView;

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            if (url.startsWith(URL_PREFIX_DO_HOMEWORK)) {
                bQuestions.setVisibility(View.VISIBLE);
            } else if (url.startsWith(URL_PREFIX_STUDY_VIDEO)) {
                webView.loadUrl(JS_AUTO_PLAY);
                webView.loadUrl(JS_ZOOM);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
            bQuestions.setVisibility(View.INVISIBLE);
            llQuestions.removeAllViews();
            bringWorkButtonToFront(bMatch);
            bAnswer.setEnabled(false);
            number = 0;
            super.onPageStarted(view, url, favicon);
        }
    };

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
    @SuppressLint("DefaultLocale")
    private void answer(int number, String answer) {
        webView.evaluateJavascript(String.format(JS_ANSWERS, number, answer.replace("\n", "").replace("\"", "'")),
                value -> ((TextView) (llQuestions.getChildAt(number - 1)).findViewById(R.id.tv_answer)).setText(value.substring(1, value.length() - 1).replace("\\n", "\n")));
    }

    private void bringWorkButtonToFront(View view) {
        bAnswer.setVisibility(view == bAnswer ? View.VISIBLE : View.GONE);
        bMatch.setVisibility(view == bMatch ? View.VISIBLE : View.GONE);
    }

    public void copyQuestion(View v) {
        setNumber((int) v.getTag());
        copyQuestion(number, ((TextView) v).getText().toString());
        bAnswer.setEnabled(true);
    }

    /**
     * Copy a question.
     */
    @SuppressLint("DefaultLocale")
    private void copyQuestion(int number, String question) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", question));
        Toast.makeText(this, String.format("已复制第 %d 题题目", number), Toast.LENGTH_SHORT).show();
    }

    public void matchHomework(View view) {
        bringWorkButtonToFront(bAnswer);
        matchHomework();
    }

    /**
     * Get all questions from web.
     */
    private void matchHomework() {
        webView.evaluateJavascript(JS_QUESTIONS, value -> {
            String[] qAndA;
            if (!"null".equals(value) && (qAndA = value.substring(1, value.length() - 1).replace("\\n", "\n").split(",,,")).length == 2) {
                // qAndA[0] - all questions.
                // qAndA[1] - all answers.
                llQuestions.removeAllViews();
                questions = qAndA[0].split(",,");
                showQuestions(questions, qAndA[1].split(",,"));
            } else {
                runOnUiThread(() -> bringWorkButtonToFront(bMatch));
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAnswer = findViewById(R.id.b_answer);
        bMatch = findViewById(R.id.b_match);
        bQuestions = findViewById(R.id.b_questions);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        layoutInflater = LayoutInflater.from(this);
        llQuestions = findViewById(R.id.ll_questions);
        llWork = findViewById(R.id.ll_work);
        llWebView = findViewById(R.id.ll_wv);
        svQuestions = findViewById(R.id.sv_questions);

        webView = new MediaWebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LinearLayout) findViewById(R.id.ll_wv)).addView(webView);

        WebSettings ws = webView.getSettings();
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(false);
        ws.setBlockNetworkImage(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setSupportMultipleWindows(false);
        ws.setSupportZoom(true);
        ws.setUseWideViewPort(true);
        ws.setUserAgentString("Chrome");
        webView.setWebViewClient(webViewClient);
        webView.requestFocusFromTouch();

        webView.loadUrl("https://onlineweb.zhihuishu.com/onlinestuh5");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            bringWorkButtonToFront(bMatch);
            bQuestions.setVisibility(View.INVISIBLE);
            webView.goBack();
            if (llWebView.getVisibility() == View.GONE) {
                llWork.setVisibility(View.GONE);
                llWebView.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
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
        llWork.setVisibility(llWork.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        llWebView.setVisibility(llWebView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     * Show questions.
     */
    @SuppressLint("DefaultLocale")
    private void showQuestions(String[] questions, String[] answers) {
        for (int i = 0; i < questions.length; ++i) {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.question, null);

            // Question
            TextView tvQuestion = layout.findViewById(R.id.tv_question);
            int number = i + 1;
            tvQuestion.setText(String.format("%d %s", number, questions[i]));
            tvQuestion.setTag(number);

            // Answer
            ((TextView) layout.findViewById(R.id.tv_answer)).setText(answers[i]);

            llQuestions.addView(layout.findViewById(R.id.ll_question));
        }
    }
}