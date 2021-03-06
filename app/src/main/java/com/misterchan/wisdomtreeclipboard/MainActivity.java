package com.misterchan.wisdomtreeclipboard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
import android.widget.SeekBar;
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

    private class GettingMymukeDocument extends Thread {
        @Override
        public void run() {
            try {
                document = getDocument(new URL(URL_PREFIX_MYMUKE + URLEncoder.encode(courseName, CHARSET_UTF8)));
            } catch (MalformedURLException | UnsupportedEncodingException e) {
            }
            runOnUiThread(() -> {
                if (document.isEmpty()) {
                    Toast.makeText(MainActivity.this, "获取题库失败", Toast.LENGTH_SHORT).show();
                    bAutoAnswer.setEnabled(true);
                    return;
                }
                webView.loadUrl(JS_AUTO_ANSWER);
            });
        }
    }

    private class GettingTikunetDocument extends Thread {
        @Override
        public void run() {
            try {
                document = getDocument(new URL(URL_PREFIX_TIKUNET + URLEncoder.encode(courseName, CHARSET_UTF8)));
            } catch (MalformedURLException | UnsupportedEncodingException e) {
            }
            runOnUiThread(() -> {
                if (document.isEmpty()) {
                    Toast.makeText(MainActivity.this, "获取题库失败", Toast.LENGTH_SHORT).show();
                    bAutoAnswerTm.setEnabled(true);
                    return;
                }
                webView.loadUrl(JS_AUTO_ANSWER_TM);
            });
        }
    }

    private static final String CHARSET_UTF8 = "UTF-8";

    private static final String KEY_LOCATION_NEXT_VIDEO = "nextVideo";
    private static final String KEY_LOCATION_RATE = "rate";
    private static final String KEY_LOCATION_RATE_LIST = "rateList";

    private static final String URL_PREFIX_EXAM = "https://onlineexamh5new.zhihuishu.com/stuExamWeb.html#/webExamList/";
    private static final String URL_PREFIX_LIVE = "https://lc.zhihuishu.com/live/vod_room.html";
    private static final String URL_PREFIX_MYMUKE = "https://www.mymuke.com/?search_cat=5&s=";
    private static final String URL_PREFIX_STUDY_VIDEO = "https://studyh5.zhihuishu.com/videoStudy.html#/";
    private static final String URL_PREFIX_TIKUNET = "http://www.tikunet.com/?s=";

    private static final String JS_ANSWERS = "" +
            "(() => {" +
            "    let result = answer(%d, \"%s\");" +
            "    timer = setInterval(timerTask, 500);" +
            "    return result;" +
            "})()";

    private static final String JS_ATTACH_SHADOW_UNDEFINED = "javascript:" +
            "Element.prototype.attachShadow = undefined;";

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
            "    let title = '见面课：' + document.getElementsByClassName('fl titleLength')[0].textContent.trim().replace('--', '–');" +
            "    let ps = doc.getElementsByClassName('content22')[0].getElementsByTagName('p');" +
            "    let answerSpans = [];" +
            "    for (let i = 0, toc; (toc = doc.getElementsByName('toc-' + (i + 1))).length > 0; ++i) {" +
            "        if (toc[0].textContent == title) {" +
            "            if (i == 0) {" +
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
            "    let playbackRate = mainActivity.getPlaybackRateLive();" +
            "    if (player.playbackRate != playbackRate) {" +
            "        player.playbackRate = playbackRate;" +
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

    private static final String JS_DEF_ANSWER_FUNC = "javascript:" +
            "const CORRECT = /(?:√|对|正确)$/, INCORRECT = /(?:×|错|错误)$/;" +
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
            "    if (document.getElementsByClassName('yidun_modal').length > 0) {" +
            "        mainActivity.showToast('请进行验证');" +
            "        return null;" +
            "    }" +
            "    let result = '';" +
            "    let questionSubject = document.getElementsByClassName('examPaper_subject mt20')[number - 1];" +
            "    let type, rb, cb, j, e;" +
            "    if ((type = questionSubject.getElementsByClassName('subject_type')).length > 0) {" +
            "        type = type[0].children[0].textContent;" +
            "        rb = '【单选题】'; cb = '【多选题】'; j = '【判断题】'; e = '【填空题】';" +
            "    } else if ((type = questionSubject.getElementsByClassName('smallStem_describe')).length > 0) {" +
            "        type = type[0].children[1].textContent.match(/单选|多选|判断|填空/)[0];" +
            "        rb = '单选'; cb = '多选'; j = '判断'; e = '填空';" +
            "    }" +
            "    switch (type) {" +
            "        case rb:" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            ans = ans.replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[2], examQuestionsAnswerTextContent = examQuestionsAnswer.textContent.replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "                result += answerNodeLabs[i].textContent;" +
            "                if (examQuestionsAnswerTextContent == ans || examQuestionsAnswerTextContent == '√' && CORRECT.test(ans) || examQuestionsAnswerTextContent == '×' && INCORRECT.test(ans)) {" +
            "                    answerNodeLabs[i].click();" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case cb:" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            ans = ans.replace(/\\s/g, '');" +
            "            for (let i = 0; i < answerNodeLabs.length; ++i) {" +
            "                let examQuestionsAnswer = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[1];" +
            "                result += answerNodeLabs[i].textContent;" +
            "                let b = ans.includes(examQuestionsAnswer.textContent.replace(/\\s/g, '').replace(/[;。；]$/, ''));" +
            "                if (b ^ examQuestionsAnswer.className.includes('onChecked')) {" +
            "                    answerNodeLabsToBeClicked.push(answerNodeLabs[i]);" +
            "                }" +
            "                if (b) {" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case j:" +
            "            var answerNodeLabs = questionSubject.getElementsByClassName('nodeLab');" +
            "            if (CORRECT.test(ans)) {" +
            "                for (let i of [0, 1]) {" +
            "                    let examQuestionsAnswerTextContent = answerNodeLabs[i].getElementsByClassName('label clearfix')[0].children[1].textContent;" +
            "                    if (examQuestionsAnswerTextContent == '对') {" +
            "                        answerNodeLabs[i].click();" +
            "                        break;" +
            "                    }" +
            "                }" +
            "                result += '对';" +
            "            } else if (INCORRECT.test(ans)) {" +
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
            "        case e:" +
            "            result += ans;" +
            "            break;" +
            "    }" +
            "    return result;" +
            "}";

    private static final String JS_OPEN_SHADOW = "javascript:" +
            "Element.prototype._attachShadow = Element.prototype.attachShadow;" +
            "Element.prototype.attachShadow = function () {" +
            "    return this._attachShadow({mode:'open'});" +
            "};";

    private static final String JS_QUESTIONS = "" +
            "var questions = [];" +
            "(() => {" +
            "    mainActivity.setCourseName(document.getElementsByClassName('course_name')[0].textContent);" +
            "    let questionSubjects = document.getElementsByClassName('examPaper_subject mt20'), answers = [],i=0;" +
            "    for (let questionSubject of questionSubjects) {" +
            "        let describe, type, rb, cb, j, e;" +
            "        if ((describe = questionSubject.getElementsByClassName('subject_describe')).length > 0) {" +
            "            questions.push(describe[0].firstChild.firstChild.textContent);" +
            "            type = questionSubject.getElementsByClassName('subject_type')[0].children[0].textContent;" +
            "            rb = '【单选题】'; cb = '【多选题】'; j = '【判断题】'; e = '【填空题】';" +
            "        } else if ((describe = questionSubject.getElementsByClassName('smallStem_describe')).length > 0) {" +
            "            let q = describe[0].children[1].textContent;" +
            "            questions.push(q);" +
            "            type = q.match(/单选|多选|判断|填空/)[0];" +
            "            rb = '单选'; cb = '多选'; j = '判断'; e = '填空';" +
            "        } else {" +
            "            break;" +
            "        }" +
            "        let ans = '  ';" +
            "        switch (type) {" +
            "            case rb:" +
            "                var answerNodeLab = questionSubject.getElementsByClassName('nodeLab nodeLab12');" +
            "                ans = '';" +
            "                for (let j = 0; j < answerNodeLab.length; ++j) {" +
            "                    ans += answerNodeLab[j].textContent + '\\n';" +
            "                }" +
            "                ans = ans.slice(0, -1);" +
            "                break;" +
            "            case cb:" +
            "                var answerNodeLab = questionSubject.getElementsByClassName('nodeLab');" +
            "                ans = '';" +
            "                for (let j = 0; j < answerNodeLab.length; ++j) {" +
            "                    ans += answerNodeLab[j].textContent + '\\n';" +
            "                }" +
            "                ans = ans.slice(0, -1);" +
            "                break;" +
            "            case j:" +
            "                ans = '对？错？';" +
            "                break;" +
            "            case e:" +
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
    private float nextVidBtnLocX, nextVidBtnLocY;
    private float rateBtnLocX, rateBtnLocY;
    private float rateListBtnLocX, rateListBtnLocY;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llAnswer;
    private LinearLayout llExamControls;
    private LinearLayout llLiveControls;
    private LinearLayout llMatch;
    private LinearLayout llQuestions;
    private LinearLayout llStudyVideoControls;
    private LinearLayout llWork;
    private LinearLayout llWebView;
    private ScrollView svQuestions;
    private SeekBar sbPlaybackRateLive;
    private SharedPreferences buttonLocations;
    private String[] questions;
    private String courseName = "";
    private String document = "";
    private String buttonLocationToBeEdited = "";
    private TextView tvPlaybackRateLive;
    private WebView webView;

    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener onWebViewTouchListener = (v, event) -> {
        if (buttonLocationToBeEdited.isEmpty() || event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        float x = event.getX(), y = event.getY();
        SharedPreferences.Editor editor = buttonLocations.edit();
        switch (buttonLocationToBeEdited) {
            case KEY_LOCATION_NEXT_VIDEO:
                buttonLocationToBeEdited = "";
                nextVidBtnLocX = x;
                nextVidBtnLocY = y;
                editor.putFloat(KEY_LOCATION_NEXT_VIDEO + "X", x);
                editor.putFloat(KEY_LOCATION_NEXT_VIDEO + "Y", y);
                editor.apply();
                Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
                break;
            case KEY_LOCATION_RATE:
                buttonLocationToBeEdited = "";
                rateBtnLocX = x;
                rateBtnLocY = y;
                editor.putFloat(KEY_LOCATION_RATE + "X", x);
                editor.putFloat(KEY_LOCATION_RATE + "Y", y);
                editor.apply();
                Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
                break;
            case KEY_LOCATION_RATE_LIST:
                buttonLocationToBeEdited = KEY_LOCATION_RATE;
                rateListBtnLocX = x;
                rateListBtnLocY = y;
                editor.putFloat(KEY_LOCATION_RATE_LIST + "X", x);
                editor.putFloat(KEY_LOCATION_RATE_LIST + "Y", y);
                break;
        }
        return false;
    };

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            if (url.startsWith(URL_PREFIX_STUDY_VIDEO)) {
                webView.loadUrl(JS_AUTO_PLAY);
                webView.loadUrl(JS_ZOOM);
                bringControlsToFront(llStudyVideoControls);
            } else if (url.startsWith(URL_PREFIX_EXAM)) {
                webView.loadUrl(JS_ATTACH_SHADOW_UNDEFINED);
                bringControlsToFront(llExamControls);
                bAutoAnswerTm.setEnabled(true);
            } else if (url.startsWith(URL_PREFIX_LIVE)) {
                webView.loadUrl(JS_AUTO_PLAY_LIVE);
                bringControlsToFront(llLiveControls);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
            bringControlsToFront();
            buttonLocationToBeEdited = "";
            llQuestions.removeAllViews();
            bringWorkButtonsToFront(llMatch);
            bAnswer.setEnabled(false);
            number = 0;
            super.onPageStarted(view, url, favicon);
        }
    };

    /**
     * Answer a question.
     */
    @SuppressLint("DefaultLocale")
    public void answer(View view) {
        if (clipboardManager.hasPrimaryClip()) {

            // Answer
            webView.evaluateJavascript(
                    String.format(JS_ANSWERS, number,
                            clipboardManager.getPrimaryClip().getItemAt(0).getText().toString()
                                    .trim().replace("\n", "").replace("\"", "'")),
                    value -> {
                        if ("null".equals(value)) {
                            return;
                        }
                        ((TextView) (llQuestions.getChildAt(number - 1)).findViewById(R.id.tv_answer))
                                .setText(value.substring(1, value.length() - 1).replace("\\n", "\n"));

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
                    });

        }
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
        llStudyVideoControls.setVisibility(View.INVISIBLE);
        llLiveControls.setVisibility(View.INVISIBLE);
        llExamControls.setVisibility(View.INVISIBLE);
    }

    private void bringControlsToFront(View view) {
        llStudyVideoControls.setVisibility(view == llStudyVideoControls ? View.VISIBLE : View.INVISIBLE);
        llLiveControls.setVisibility(view == llLiveControls ? View.VISIBLE : View.INVISIBLE);
        llExamControls.setVisibility(view == llExamControls ? View.VISIBLE : View.INVISIBLE);
    }

    private void bringWorkButtonsToFront(View view) {
        llAnswer.setVisibility(view == llAnswer ? View.VISIBLE : View.GONE);
        llMatch.setVisibility(view == llMatch ? View.VISIBLE : View.GONE);
    }

    public void copyAnswer(View v) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", ((TextView) v).getText()));
        Toast.makeText(this, String.format("已复制第 %d 题选项", number), Toast.LENGTH_SHORT).show();
    }

    public void copyQuestion(View v) {
        int number = (int) v.getTag();
        setNumber(number);
        copyQuestion(number, questions[number - 1]);
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

    public void editNextVidBtnLoc(View view) {
        buttonLocationToBeEdited = KEY_LOCATION_NEXT_VIDEO;
    }

    public void editRateBtnLoc(View view) {
        buttonLocationToBeEdited = KEY_LOCATION_RATE_LIST;
    }

    @JavascriptInterface
    public String getDocument() {
        return document;
    }

    private String getDocument(URL url) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            String urlStr = url.toString();
            if (urlStr.startsWith(URL_PREFIX_MYMUKE)) {
                bAutoAnswer.setEnabled(true);
            } else if (urlStr.startsWith(URL_PREFIX_TIKUNET)) {
                bAutoAnswerTm.setEnabled(true);
            }
        }
        return sb.toString();
    }

    @JavascriptInterface
    public float getPlaybackRateLive() {
        return sbPlaybackRateLive.getProgress() / 2.0f;
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
            Log.d("\n\n\nvalue\n\n\n", value + "\n\n\n");
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
        if (!buttonLocationToBeEdited.isEmpty()) {
            return;
        }
        touch(webView, 50.0f, 1000.0f);
        Thread.sleep(500L);
        touch(webView, nextVidBtnLocX, nextVidBtnLocY);
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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAnswer = findViewById(R.id.b_answer);
        bAutoAnswer = findViewById(R.id.b_auto_answer);
        bAutoAnswerTm = findViewById(R.id.b_auto_answer_tm);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        buttonLocations = getSharedPreferences("Locations", MODE_PRIVATE);
        layoutInflater = LayoutInflater.from(this);
        llAnswer = findViewById(R.id.ll_answer);
        llExamControls = findViewById(R.id.ll_exam_controls);
        llLiveControls = findViewById(R.id.ll_live_controls);
        llMatch = findViewById(R.id.ll_match);
        llQuestions = findViewById(R.id.ll_questions);
        llStudyVideoControls = findViewById(R.id.ll_study_video_controls);
        llWork = findViewById(R.id.ll_work);
        llWebView = findViewById(R.id.ll_wv);
        sbPlaybackRateLive = findViewById(R.id.sb_playback_rate_live);
        svQuestions = findViewById(R.id.sv_questions);
        tvPlaybackRateLive = findViewById(R.id.tv_playback_rate_live);

        webView = new MediaWebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setOnTouchListener(onWebViewTouchListener);
        ((LinearLayout) findViewById(R.id.ll_wv)).addView(webView);

        sbPlaybackRateLive.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPlaybackRateLive.setText(String.valueOf(progress / 2.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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

        nextVidBtnLocX = buttonLocations.getFloat(KEY_LOCATION_NEXT_VIDEO + "X", 50.0f);
        nextVidBtnLocY = buttonLocations.getFloat(KEY_LOCATION_NEXT_VIDEO + "Y", 1895.0f);
        rateBtnLocX = buttonLocations.getFloat(KEY_LOCATION_RATE + "X", 730.0f);
        rateBtnLocY = buttonLocations.getFloat(KEY_LOCATION_RATE + "Y", 1795.0f);
        rateListBtnLocX = buttonLocations.getFloat(KEY_LOCATION_RATE_LIST + "X", 730.0f);
        rateListBtnLocY = buttonLocations.getFloat(KEY_LOCATION_RATE_LIST + "Y", 1895.0f);

        webView.loadUrl("https://onlineweb.zhihuishu.com/onlinestuh5");
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

    @JavascriptInterface
    public void toggleQuestionViewVisibility() {
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
        if (!buttonLocationToBeEdited.isEmpty()) {
            return;
        }
        touch(webView, rateListBtnLocX, rateListBtnLocY);
        Thread.sleep(500L);
        touch(webView, rateBtnLocX, rateBtnLocY);
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

    @JavascriptInterface
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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