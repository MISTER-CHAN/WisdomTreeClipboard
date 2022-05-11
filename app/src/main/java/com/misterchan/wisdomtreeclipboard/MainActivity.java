package com.misterchan.wisdomtreeclipboard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String CHARSET_UTF8 = "UTF-8";

    private static final String URL_PREFIX_DO_HOMEWORK = "https://onlineexamh5new.zhihuishu.com/stuExamWeb.html#/webExamList/dohomework/";
    private static final String URL_PREFIX_LIVE = "https://lc.zhihuishu.com/live/vod_room.html";
    private static final String URL_PREFIX_MYMUKE = "https://www.mymuke.com/?search_cat=5&s=";
    private static final String URL_PREFIX_STUDY_VIDEO = "https://studyh5.zhihuishu.com/videoStudy.html#/";
    private static final String URL_PREFIX_TIKUNET = "http://www.tikunet.com/?s=";

    private static final String JS_DEF_ANSWER_FUNC = "javascript:" +
            "const CORRECT = ['√', '对', '正确'], INCORRECT = ['×', '错', '错误'];" +
            "var answerNodeLabsToBeClicked = [], timer = {};" +
            "function timerTask() {" +
            "    if (answerNodeLabsToBeClicked.length > 0) {" +
            "        answerNodeLabsToBeClicked[0].click();" +
            "        answerNodeLabsToBeClicked.shift();" +
            "    } else {" +
            "        clearInterval(timer);" +
            "    }" +
            "};" +
            "function answer(number, ans) {" +
            "    let result = '';" +
            "    let questionSubject = document.getElementsByClassName('examPaper_subject mt20')[number - 1];" +
            "    switch (questionSubject.getElementsByClassName('subject_type')[0].children[0].textContent) {" +
            "        case '【单选题】':" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            ans = ans.replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[2], examQuestionsAnswerTextContent = examQuestionsAnswer.textContent.replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "                result += answerNodeLabs[i].textContent;" +
            "                if (examQuestionsAnswerTextContent == ans || examQuestionsAnswerTextContent == '√' && CORRECT.includes(ans) || examQuestionsAnswerTextContent == '×' && INCORRECT.includes(ans)) {" +
            "                    answerNodeLabs[i].click();" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case '【多选题】':" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            ans = ans.replace(/\\s/g, '');" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[1];" +
            "                result += answerNodeLabs[i].textContent;" +
            "                let b = ans.includes(examQuestionsAnswer.textContent.replace(/\\s/g, '').replace(/[;。；]$/, ''));" +
            "                if (b ^ examQuestionsAnswer.className == 'node_detail examquestions-answer fl onChecked') {" +
            "                    answerNodeLabsToBeClicked.push(answerNodeLabs[i]);" +
            "                }" +
            "                if (b) {" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case '【判断题】':" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            if (CORRECT.includes(ans)) {" +
            "                for (let i of [0, 1]) {" +
            "                    let examQuestionsAnswerTextContent = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[1].textContent;" +
            "                    if (examQuestionsAnswerTextContent == '对') {" +
            "                        answerNodeLabs[i].click();" +
            "                        break;" +
            "                    }" +
            "                }" +
            "                result += '对';" +
            "            } else if (INCORRECT.includes(ans)) {" +
            "                for (let i of [0, 1]) {" +
            "                    let examQuestionsAnswerTextContent = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[1].textContent;" +
            "                    if (examQuestionsAnswerTextContent == '错') {" +
            "                        answerNodeLabs[i].click();" +
            "                        break;" +
            "                    }" +
            "                }" +
            "                result += '错';" +
            "            }" +
            "            break;" +
            "        case '【填空题】':" +
            "            result += ans;" +
            "            break;" +
            "    }" +
            "    return result;" +
            "}";

    private static final String JS_ANSWERS = "" +
            "(() => {" +
            "    let result = answer(%d, \"%s\");" +
            "    timer = setInterval(timerTask, 500);" +
            "    return result;" +
            "})()";

    private static final String JS_AUTO_ANSWER = "javascript:" +
            "(() => {" +
            "    if (typeof questions == 'undefined' || questions.length == 0) {" +
            "        return;" +
            "    }" +
            "    let doc = new DOMParser().parseFromString(mainActivity.getDocument(), 'text/html');" +
            "    let content = doc.getElementsByClassName('content22')[0].children;" +
            "    if (content.length != questions.length) {" +
            "        return;" +
            "    }" +
            "    for (let i = 0; i < questions.length; ++i) {" +
            "        answer(i + 1, content[i].getElementsByTagName('span')[0].textContent);" +
            "    }" +
            "    timer = setInterval(timerTask, 500);" +
            "})();";

    private static final String JS_AUTO_ANSWER_TM = "javascript:" +
            "(() => {" +
            "    if (typeof questions == 'undefined' || questions.length == 0) {" +
            "        return;" +
            "    }" +
            "    let doc = new DOMParser().parseFromString(mainActivity.getDocument(), 'text/html');" +
            "    let title = '见面课：' + document.getElementsByClassName('fl titleLength')[0].textContent;" +
            "    let ps = doc.getElementsByClassName('content22')[0].getElementsByTagName('p');" +
            "    let answerSpans = [];" +
            "    for (let i = 1, toc; (toc = doc.getElementsByName('toc-' + i)).length > 0; ++i) {" +
            "        if (toc[0].textContent == title) {" +
            "            if (--i == 0) {" +
            "                let spans = ps[0].getElementsByTagName('span');" +
            "                for (let span of spans) {" +
            "                    if (span.getAttribute('style') == 'color:red !important') {" +
            "                        answerSpans.push(span);" +
            "                    }" +
            "                }" +
            "            } else {" +
            "                answerSpans = ps[i].getElementsByTagName('span');" +
            "            }" +
            "            break;" +
            "        }" +
            "    };" +
            "    for (let i = 0; i < questions.length; ++i) {" +
            "        answer(i + 1, answerSpans[i].textContent);" +
            "    }" +
            "    timer = setInterval(timerTask, 500);" +
            "})();";

    private static final String JS_AUTO_PLAY = "javascript:" +
            "var isPlaying = false;" +
            "setInterval(() => {" +
            "    let player = document.getElementById('vjs_container_html5_api');" +
            "    if (player == null) {" +
            "        return;" +
            "    }" +
            "" +
            "    if (player.playbackRate != 0) {" +
            "        player.playbackRate = 0;" +
            "    }" +
            "" +
            "    let readDialog = document.getElementsByClassName('dialog-read')[0];" +
            "    if (readDialog != undefined) {" +
            "        readDialog.getElementsByClassName('iconguanbi')[0].click();" +
            "    }" +
            "" +
            "    let testDialog = document.getElementsByClassName('dialog-test')[0];" +
            "    if (testDialog != undefined) {" +
            "        testDialog.getElementsByClassName('topic-item')[0].click();" +
            "        testDialog.getElementsByClassName('el-dialog__headerbtn')[0].click();" +
            "        player.play();" +
            "    }" +
            "" +
//            "    let defini = document.getElementsByClassName('definiBox')[0];" +
//            "    if (defini != undefined && defini.firstChild.textContent != '流畅') {" +
//            "        defini.getElementsByClassName('line1bq switchLine')[0].click();" +
//            "    }" +
            "" +
            "    let speedBox = document.getElementsByClassName('speedBox')[0];" +
            "    if (speedBox != undefined && speedBox.textContent != 'X 1.5X 1.5X 1.25X 1.0') {" +
            "        let speedTab = document.getElementsByClassName('speedTab speedTab15')[0];" +
            "        speedTab.setAttribute('rate', 15);" +
//            "        speedTab.click();" +
            "        mainActivity.setPlaybackRate();" +
            "    }" +
            "" +
            "    let currentPlay = document.getElementsByClassName('clearfix video current_play')[0];" +
            "    if (currentPlay.getElementsByClassName('fl time_icofinish').length > 0) {" +
            "        if (isPlaying) {" +
            "            isPlaying = false;" +
//            "            document.getElementById('nextBtn').click();" +
            "            mainActivity.nextVideo();" +
            "        }" +
            "    } else {" +
            "        isPlaying = true;" +
            "    }" +
            "" +
            "    if (document.getElementById('playButton').getAttribute('class') == 'playButton') {" +
            "        player.play();" +
            "    }" +
            "" +
            "}, 3000);";

    private static final String JS_AUTO_PLAY_LIVE = "javascript:" +
            "setInterval(() => {" +
            "" +
            "    let player = document.getElementById('vjs_forFollowBackDiv_html5_api');" +
            "" +
            "    let definiLine = document.getElementsByClassName('line1bq')[0];" +
            "    if (definiLine != undefined && !definiLine.className.includes('active')) {" +
            "        definiLine.click();" +
            "    }" +
            "" +
            "    if (player.playbackRate != 12) {" +
            "        player.playbackRate = 12;" +
            "    }" +
            "" +
            "    if (player.currentTime >= 60) {" +
            "        let currentPlayer = document.getElementsByClassName('current_player')[0];" +
            "        if (currentPlayer != undefined) {" +
            "            let start = videoStartEndmap.get(currentPlayer.getAttribute('id').substring(8))[0];" +
            "            let time = Math.floor(player.currentTime / 60);" +
            "            if (!isWatch(start + time)) {" +
            "                player.currentTime = (time - 1) * 60;" +
            "            }" +
            "        }" +
            "    }" +
            "" +
            "}, 1000);";

    private static final String JS_OPEN_SHADOW = "javascript:" +
            "Element.prototype._attachShadow = Element.prototype.attachShadow;" +
            "Element.prototype.attachShadow = function () {" +
            "    return this._attachShadow({mode:'open'});" +
            "};";

    private static final String JS_QUESTIONS = "" +
            "var questions = [];" +
            "(() => {" +
            "    mainActivity.setCourseName(document.getElementsByClassName('course_name')[0].textContent);" +
            "    let questionSubjects = document.getElementsByClassName('examPaper_subject mt20'), answers = [];" +
            "    for (let questionSubject of questionSubjects) {" +
            "        questions.push(questionSubject.getElementsByClassName('subject_describe dynamic-fonts')[0].firstChild.firstChild.shadowRoot.textContent);" +
            "        let ans = '  ';" +
            "        switch (questionSubject.getElementsByClassName('subject_type')[0].children[0].textContent) {" +
            "            case '【单选题】':" +
            "                var answerNodeLab = questionSubject.getElementsByClassName('nodeLab nodeLab12');" +
            "                for (let j = 0; j < answerNodeLab.length; ++j) {" +
            "                    ans += answerNodeLab[j].textContent + '\\n';" +
            "                }" +
            "                ans = ans.slice(0, -1);" +
            "                break;" +
            "            case '【多选题】':" +
            "                var answerNodeLab = questionSubject.getElementsByClassName('nodeLab');" +
            "                for (let j = 0; j < answerNodeLab.length; ++j) {" +
            "                    ans += answerNodeLab[j].textContent + '\\n';" +
            "                }" +
            "                ans = ans.slice(0, -1);" +
            "                break;" +
            "            case '【判断题】':" +
            "                ans = '对？错？';" +
            "                break;" +
            "            case '【填空题】':" +
            "                ans = ' ';" +
            "                break;" +
            "        }" +
            "        answers.push(ans);" +
            "    }" +
            "    return questions.join(',,') + ',,,' + answers.join(',,');" +
            "})()";

    private static final String JS_ZOOM = "javascript:" +
            "document.getElementsByName('viewport')[0].setAttribute('content','width=device-width,initial-scale=0.25')";

    private Button bAnswer;
    private Button bAutoAnswer;
    private Button bAutoAnswerTm;
    private ClipboardManager clipboardManager;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llAnswer;
    private LinearLayout llHomeworkControls;
    private LinearLayout llLiveControls;
    private LinearLayout llMatch;
    private LinearLayout llQuestions;
    private LinearLayout llWork;
    private LinearLayout llWebView;
    private ScrollView svQuestions;
    private String[] questions;
    private String courseName = "";
    public String document = "";
    private WebView webView;

    @JavascriptInterface
    public String getDocument() {
        return document;
    }

    private class GettingMymukeDocument extends Thread {
        @Override
        public void run() {
            try {
                document = getDocument(new URL(URL_PREFIX_MYMUKE + URLEncoder.encode(courseName, CHARSET_UTF8)));
            } catch (MalformedURLException | UnsupportedEncodingException e) {}
            runOnUiThread(() -> {
                if (document.isEmpty()) {
                    Toast.makeText(MainActivity.this, "获取题库失败", Toast.LENGTH_SHORT).show();
                    bAutoAnswer.setEnabled(true);
                    return;
                }
                webView.loadUrl(JS_AUTO_ANSWER);
            });
        }
    };

    private class GettingTikunetDocument extends Thread {
        @Override
        public void run() {
            try {
                document = getDocument(new URL(URL_PREFIX_TIKUNET + URLEncoder.encode(courseName, CHARSET_UTF8)));
            } catch (MalformedURLException | UnsupportedEncodingException e) {}
            runOnUiThread(() -> {
                if (document.isEmpty()) {
                    Toast.makeText(MainActivity.this, "获取题库失败", Toast.LENGTH_SHORT).show();
                    bAutoAnswerTm.setEnabled(true);
                    return;
                }
                webView.loadUrl(JS_AUTO_ANSWER_TM);
            });
        }
    };

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            if (url.startsWith(URL_PREFIX_STUDY_VIDEO)) {
                webView.loadUrl(JS_AUTO_PLAY);
                webView.loadUrl(JS_ZOOM);
            } else if (url.startsWith(URL_PREFIX_DO_HOMEWORK)) {
                bringControlsToFront(llHomeworkControls);
                bAutoAnswerTm.setEnabled(true);
            } else if (url.startsWith(URL_PREFIX_LIVE)) {
                webView.loadUrl(JS_AUTO_PLAY_LIVE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
            bringControlsToFront();
            llQuestions.removeAllViews();
            bringWorkButtonsToFront(llMatch);
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

    public void autoAnswer(View view) {
        bAutoAnswer.setEnabled(false);
        Toast.makeText(this, "搜题中，请稍候……", Toast.LENGTH_SHORT).show();
        toggleQuestionViewVisibility();
        new GettingMymukeDocument().start();
    }

    public void autoAnswerTm(View view) {
        bAutoAnswerTm.setEnabled(false);
        Toast.makeText(this, "搜题中，请稍候……", Toast.LENGTH_SHORT).show();
        toggleQuestionViewVisibility();
        new GettingTikunetDocument().start();
    }

    private void bringControlsToFront() {
        llLiveControls.setVisibility(View.INVISIBLE);
        llHomeworkControls.setVisibility(View.INVISIBLE);
    }

    private void bringControlsToFront(View view) {
        llLiveControls.setVisibility(view == llLiveControls ? View.VISIBLE : View.INVISIBLE);
        llHomeworkControls.setVisibility(view == llHomeworkControls ? View.VISIBLE : View.INVISIBLE);
    }

    private void bringWorkButtonsToFront(View view) {
        llAnswer.setVisibility(view == llAnswer ? View.VISIBLE : View.GONE);
        llMatch.setVisibility(view == llMatch ? View.VISIBLE : View.GONE);
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

    private String getDocument(URL url) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void matchHomework(View view) {
        bringWorkButtonsToFront(llAnswer);
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
                webView.loadUrl(JS_DEF_ANSWER_FUNC);
            } else {
                runOnUiThread(() -> bringWorkButtonsToFront(llMatch));
            }
        });
    }

    @JavascriptInterface
    public void nextVideo() throws InterruptedException {
        touch(webView, 50f, 1000f);
        Thread.sleep(500L);
        touch(webView, 50f, 1895f);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAnswer = findViewById(R.id.b_answer);
        bAutoAnswer = findViewById(R.id.b_auto_answer);
        bAutoAnswerTm = findViewById(R.id.b_auto_answer_tm);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        layoutInflater = LayoutInflater.from(this);
        llAnswer = findViewById(R.id.ll_answer);
        llLiveControls = findViewById(R.id.ll_live_controls);
        llMatch = findViewById(R.id.ll_match);
        llQuestions = findViewById(R.id.ll_questions);
        llHomeworkControls = findViewById(R.id.ll_homework_controls);
        llWork = findViewById(R.id.ll_work);
        llWebView = findViewById(R.id.ll_wv);
        svQuestions = findViewById(R.id.sv_questions);

        webView = new MediaWebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LinearLayout) findViewById(R.id.ll_wv)).addView(webView);

        webView.addJavascriptInterface(this, "mainActivity");
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
            bringControlsToFront();
            bringWorkButtonsToFront(llMatch);
            webView.goBack();
            if (llWebView.getVisibility() == View.GONE) {
                llWork.setVisibility(View.GONE);
                llWebView.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    public void openShadow(View view) {
        webView.loadUrl(JS_OPEN_SHADOW);
    }

    public void reload(View view) {
        webView.reload();
        llWork.setVisibility(View.GONE);
        llWebView.setVisibility(View.VISIBLE);
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

    public void toggleQuestionViewVisibility(View v) {
        toggleQuestionViewVisibility();
    }

    private void toggleQuestionViewVisibility() {
        llWork.setVisibility(llWork.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        llWebView.setVisibility(llWebView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @JavascriptInterface
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setLivePlaybackRate(View view) {
        webView.loadUrl(JS_AUTO_PLAY_LIVE);
    }

    @JavascriptInterface
    public void setPlaybackRate() throws InterruptedException {
        touch(webView, 730.0f, 1895.0f);
        Thread.sleep(500L);
        touch(webView, 730.0f, 1795.0f);
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

    private void touch(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int metaState = 0;
        MotionEvent actionDown = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);
        view.dispatchTouchEvent(actionDown);
        MotionEvent actionUp = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState);
        view.dispatchTouchEvent(actionUp);
    }
}