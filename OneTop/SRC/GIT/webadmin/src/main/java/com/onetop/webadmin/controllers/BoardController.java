package com.onetop.webadmin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.board.Notice;
import com.onetop.webadmin.models.board.Qna;
import com.onetop.webadmin.models.board.QnaComment;
import com.onetop.webadmin.responses.board.NoticeResponse;
import com.onetop.webadmin.responses.board.QnaResponse;
import com.onetop.webadmin.services.AttachmentService;
import com.onetop.webadmin.services.AuthorityService;
import com.onetop.webadmin.services.BoardService;
import com.onetop.webadmin.util.AES256Util;
import com.onetop.webadmin.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private Environment env;
    /**
     * @brief       공지사항 페이지\n
     * @details     \n
     * @param       search_type       검색타입\n
     * @param       search_content    검색내용\n
     * @param       page              페이지\n
     */
    @RequestMapping(value = "board1")
    public String board1(
            HttpSession session,
            Model model,
            @RequestParam(value = "search_type", defaultValue = "all", required = false) String search_type,
            @RequestParam(value = "search_content", defaultValue = "", required = false) String search_content,
            @RequestParam(value = "search_lang", defaultValue = "all", required = false) String search_lang,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){

        log.info("Notice Page");

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        NoticeResponse noticeResponse = null;

        try {
            noticeResponse = boardService.noticeList(search_type, search_content, search_lang, page, search_listCount);
        } catch (Exception e) {
            log.error("Fail to get noticeList, msg = " + e.getMessage());
        }

        model.addAttribute("noticeResponse", noticeResponse);
        model.addAttribute("search_type", search_type);
        model.addAttribute("search_content", search_content);
        model.addAttribute("search_lang", search_lang);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("page", page);
        return "board/board1";
    }

    /**
     * @brief       공지사항 추가/수정 페이지\n
     * @details     \n
     * @param       no    공지사항 번호\n
     */
    @RequestMapping(value = "board1_mod", method = RequestMethod.GET)
    public String board1_mod(
            HttpSession session,
            Model model,
            @RequestParam(value = "no", defaultValue = "0", required = false) int no,
            @RequestParam(value = "mode", defaultValue = "", required = false) String mode
    ){

        log.info("Notice Add/Modify Page");

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        Notice notice = new Notice();

        // 수정 페이지로 이동
        if (no != 0) {

            try {

                notice = boardService.getNoticeByNo(no);
            } catch (Exception e) {

                log.error("Fail to get notice, msg = " + e.getMessage());
            }
        } else {

            // 다음 index값을 가지고와서 넘겨준다.
            try {

                int nextNo = boardService.noticeLastIndex();
                model.addAttribute("nextNo", nextNo);
            } catch (Exception e) {

                log.error("Fail to get notice last index, msg = " + e.getMessage());
            }
        }

        String address = env.getProperty("webadmin.image.addr");
        log.info("address : [" + address + "]");
        model.addAttribute("image_addr", address);
        model.addAttribute("notice", notice);
        model.addAttribute("mode", mode);
        return "board/board1_mod";
    }

    /**
     * @brief       공지사항 추가/수정 Proc\n
     * @details     \n
     * @param       no            공지사항 번호\n
     * @param       admin_id      등록자아이디\n
     * @param       title         제목\n
     * @param       admin_name    등록자명\n
     * @param       is_secret     전시상태\n
     * @param       content       내용\n
     */
    @RequestMapping(value = "ajax/board1_mod/proc", method = RequestMethod.POST)
    public @ResponseBody
    String board1_mod_proc(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "no", defaultValue = "0", required = false) int no,
            @RequestParam(value = "admin_id", required = true) String admin_id,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "admin_name", required = true) String admin_name,
            @RequestParam(value = "is_secret", required = true) String is_secret,
            @RequestParam(value = "content", required = true) String content,
            @RequestParam(value = "language", required = true) String language,
            @RequestParam(value = "type", required = true) String type
    ){

        String result = "fail";
        String logContent = "";
        String logTemp = "";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        Notice notice = null;

        try {

            // 수정
            if (no != 0) {

                logContent = "Modify notice, no = " + no + ", ";

                // 원래 정보를 가져온다.
                notice = boardService.getNoticeByNo(no);

                if (!notice.getAdmin_id().equals(admin_id)) {

                    notice.setAdmin_id(admin_id);
                    logTemp += ", admin_id = " + admin_id;
                }

                if (!notice.getAdmin_name().equals(admin_name)) {

                    notice.setAdmin_name(admin_name);
                    logTemp += "admin_name = " + admin_name;
                }

                if (!notice.getTitle().equals(title)) {

                    notice.setTitle(title);
                    logTemp += !logTemp.equals("") ? ", title = " + title : "title = " + title;
                }

                if (!notice.getIs_secret().equals(is_secret)) {

                    notice.setIs_secret(is_secret);
                    logTemp += !logTemp.equals("") ? ", is_secret = " + is_secret : "is_secret = " + is_secret;
                }

                if (!notice.getLang().equals(language)) {

                    notice.setLang(language);
                    logTemp += !logTemp.equals("") ? ", lang = " + language : "lang = " + language;
                }

                if (!notice.getContent().equals(content)) {

                    notice.setContent(content);
                    logTemp += !logTemp.equals("") ? ", content change" : "content change";
                }

                boardService.modifyNotice(notice);

                if (!logTemp.equals("")) {

                    logContent += logTemp;
                    authorityService.insertAdminLog(request, 612, logContent, loginAdmin.getId());
                }
            }
            // 추가
            else {

                notice = new Notice();
                notice.setAdmin_id(admin_id);
                notice.setAdmin_name(admin_name);
                notice.setTitle(title);
                notice.setIs_secret(is_secret);
                notice.setContent(content);
                notice.setLang(language);
                notice.setType(type);

                int insertNo = boardService.insertNotice(notice);
                authorityService.insertAdminLog(request, 611, "Add notice, no=" + insertNo + ", title=" + title, loginAdmin.getId());
            }

            result = "success";
        } catch (Exception e) {

            log.error("Fail to save notice, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       공지사항 삭제\n
     * @details     \n
     * @param       checkNo    공지사항 번호\n
     */
    @RequestMapping(value = "ajax/board1/delete", method = RequestMethod.POST)
    public @ResponseBody String board1_delete(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "checkNo", required = true) String checkNo
    ){

        String result = "fail";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        String[] tempNo = checkNo.split(",");

        try {

            for (String no : tempNo) {

                Notice notice = boardService.getNoticeByNo(Integer.parseInt(no));
                boardService.deleteNotice(no);

                String base_url = File.separator + "notice" + File.separator + no + File.separator;
                attachmentService.deleteFile(base_url, "notice");

                authorityService.insertAdminLog(request, 613, "Delete notice, no=" + no + "title=" + notice.getTitle(), loginAdmin.getId());
            }

            result = "success";
        } catch (Exception e) {

            log.error("Fail to delete notice, msg = " + e.getMessage());
        }

        return result;
    }


    /**
     * 공지사항 메인노출 여부
     */
    @RequestMapping(value = "ajax/board1/mainview", method = RequestMethod.POST)
    public @ResponseBody String board1_main_view(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "checkViewNo", required = true) String checkViewNo,
            @RequestParam(value = "yn", required = true) String yn
    ){

        String result = "fail";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        try {
            boardService.noticeMainViewYN(checkViewNo, yn);

            authorityService.insertAdminLog(request, 614, "Update bbs.notice set main_view=" + yn + " where no=" + checkViewNo, loginAdmin.getId());

            result = "success";
        } catch (Exception e) {
            log.error("Fail to update notice, msg = " + e.getMessage());
        }

        return result;
    }


    /**
     * @brief       문의/답변 페이지\n
     * @details     \n
     * @param       search_state              검색답변상태\n
     * @param       search_reg_dt             문의등록일\n
     * @param       search_id                 검색아이디\n
     * @param       search_name               검색이름\n
     * @param       search_qna_type_first     문의구분 첫번째\n
     * @param       search_qna_type_second    문의구분 두번째\n
     * @param       search_content            검색내용\n
     * @param       page                      페이지\n
     */
    @RequestMapping(value = "board3")
    public String board3(
            HttpSession session,
            Model model,
            @RequestParam(value = "search_state", defaultValue = "all", required = false) String search_state,
            @RequestParam(value = "search_reg_dt", defaultValue = "", required = false) String search_reg_dt,
            @RequestParam(value = "search_id", defaultValue = "", required = false) String search_id,
            @RequestParam(value = "search_name", defaultValue = "", required = false) String search_name,
            @RequestParam(value = "search_qna_type_first", defaultValue = "0", required = false) int search_qna_type_first,
            @RequestParam(value = "search_qna_type_second", defaultValue = "0", required = false) int search_qna_type_second,
            @RequestParam(value = "search_content", defaultValue = "", required = false) String search_content,
            @RequestParam(value = "member_detail", defaultValue = "0", required = false) int member_detail,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){

        log.info("Qna Page");

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {

            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        QnaResponse qnaResponse = null;

        try {

            String[] reg_dts = search_reg_dt.split("-");

            if (member_detail == 1) {

                qnaResponse = boardService.qnaList(search_state, "", "", search_id, search_name, search_qna_type_first, search_qna_type_second, search_content, page, search_listCount);
            } else {

                qnaResponse = boardService.qnaList(search_state, reg_dts[0], reg_dts[1], search_id, search_name, search_qna_type_first, search_qna_type_second, search_content, page, search_listCount);
            }
        } catch (Exception e) {

            log.error("Fail to get qnaList, msg = " + e.getMessage());
        }

        model.addAttribute("qnaResponse", qnaResponse);
        model.addAttribute("search_state", search_state);
        model.addAttribute("search_reg_dt", search_reg_dt);
        model.addAttribute("search_id", search_id);
        model.addAttribute("search_name", search_name);
        model.addAttribute("search_qna_type_first", search_qna_type_first);
        model.addAttribute("search_qna_type_second", search_qna_type_second);
        model.addAttribute("search_content", search_content);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("page", page);
        return "board/board3";
    }

    /**
     * @brief       문의/답변 답변 페이지\n
     * @details     \n
     * @param       no    QNA 번호\n
     */
    @RequestMapping(value = "board3_mod", method = RequestMethod.GET)
    public String board3_mod(
            HttpSession session,
            Model model,
            @RequestParam(value = "no", defaultValue = "0", required = false) int no
    ){

        log.info("QNA Add/Modify Page");

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        Qna qna = new Qna();

        // 수정 페이지로 이동
        if (no != 0) {

            try {

                qna = boardService.getQnaByNo(no);
            } catch (Exception e) {

                log.error("Fail to get qna, msg = " + e.getMessage());
            }
        }

        List<QnaComment> qnaCommentList = new ArrayList<QnaComment>();

        // 답변도 가져와야함(qna_comment)
        try {

            qnaCommentList = boardService.qnaCommentList(no);
        } catch (Exception e) {

            log.error("Fail to get qna comment. msg = " + e.getMessage());
        }

        String address = env.getProperty("webadmin.image.addr");
        log.info("address : [" + address + "]");
        model.addAttribute("image_addr", address);
        model.addAttribute("qna", qna);
        model.addAttribute("qnaCommentList", qnaCommentList);
        return "board/board3_mod";
    }

    /**
     * @brief       문의/답변 답변가져오기\n
     * @details     \n
     * @param       no    QNA 번호\n
     */
    @RequestMapping(value = "ajax/board3_comment", method = RequestMethod.POST)
    public @ResponseBody List<QnaComment> board3_comment(
            HttpSession session,
            Model model,
            @RequestParam(value = "no", defaultValue = "0", required = false) int no
    ){

        List<QnaComment> qnaCommentList = new ArrayList<QnaComment>();

        try {

            qnaCommentList = boardService.qnaCommentList(no);
        } catch (Exception e) {

            log.error("Fail to get qna comment. msg = " + e.getMessage());
        }

        return qnaCommentList;
    }

    /**
     * @brief       문의/답변 답변 Proc\n
     * @details     \n
     * @param       no         QNA 번호\n
     * @param       comment    내용\n
     */
    @RequestMapping(value = "ajax/board3_mod/proc", method = RequestMethod.POST)
    public @ResponseBody String board3_mod_proc(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "no", required = true) int no,
            @RequestParam(value = "comment", required = true) String comment,
            @RequestParam(value = "mbId") String mbId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "contents") String contents,
            @RequestParam(value = "regDt") String regDt
    ){

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String result = "fail";

        QnaComment qnaComment = null;

        try {

            qnaComment = new QnaComment();
            qnaComment.setQna_no(no);
            qnaComment.setContent(comment);
            qnaComment.setAdmin_id(adminMember.getId());

            boardService.insertQnaComment(qnaComment);
            authorityService.insertAdminLog(request, 631, "Add qna comment, no=" + no + ", content", adminMember.getId());

            // 해당 문의/답변의 답변 상태 변경
            boardService.modifyQnaStateByNo(no);
            authorityService.insertAdminLog(request, 632, "Change qna, no=" + no + ", state = Y", adminMember.getId());

            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //String setRegDt = sdf.format(sdf.parse(regDt));

            /*
            String address = env.getProperty("webadmin.mailsender.addr");
            log.info("address : [" + address + "]");
            // EMAIL 발송의 성공/실패는 어드민에서 알 필요 없음.
            new Thread(() -> {

                HashMap<String, String> response = Utils.SendHttpRequest(
                    String.format("%s%s", address, "/ems/admin/response-qna"),
                    new JSONObject(){
                        {
                            put("mbId", mbId);
                            put("regDt", setRegDt);
                            put("title", title);
                            put("contents", contents);
                            put("comment", comment);
                        }
                    },
                    session.getAttribute("language").toString()
                );

                if(response.get("responseCode").equals("success")){
                    try{
                        System.out.println("success : " + response.get("resultData"));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.out.println("fail : " + response.get("resultData"));
                    }
                }
                else{
                    System.out.println("fail : " + response.get("resultData"));
                }
            }).start();
            */

            result = "success";
        } catch (Exception e) {

            log.error("Fail to save qna comment, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       문의/답변 첨부파일 다운로드\n
     * @details     \n
     * @param       no         문의/답변 번호\n
     * @param       fileNum    첨부파일번호\n
     */
    @RequestMapping(value = "ajax/board3_attach/download")
    public @ResponseBody byte[] attachDownload(
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response,
            @RequestParam(value = "no") int no,
            @RequestParam(value = "fileNum") String fileNum
    ) throws Exception {

        log.info("board3 download attach files");

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        byte[] fileByte = null;

        try {

            Qna qna = boardService.getQnaByNo(no);

            String fileName = "";

            AES256Util aesUtil = new AES256Util(qna.getMember_id(), new SimpleDateFormat("yyyyMMddHHmmss").format(qna.getReg_dt()));

            authorityService.insertAdminLog(request, 633, "Download qna attach file, no=" + no, loginAdmin.getId());
        } catch (Exception e) {

            log.error("Fail to download attach files, msg = " + e.getMessage());
            e.printStackTrace();
        }

        return fileByte;
    }


    /**
     * @brief       문의/답변 첨부파일 압축하여 다운로드\n
     * @details     \n
     * @param       no         문의/답변 번호\n
     */
    @RequestMapping(value = "board3/attach/download/zip")
    public @ResponseBody byte[] attachDownload(
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response,
            @RequestParam(value = "no") int no
    ) throws Exception {

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        byte[] fileByte = null;

        try {

            Qna qna = boardService.getQnaByNo(no);

            List<String> attachList = new ArrayList<String>();

            AES256Util aesUtil = new AES256Util(qna.getMember_id(), new SimpleDateFormat("yyyyMMddHHmmss").format(qna.getReg_dt()));

            authorityService.insertAdminLog(request, 633, "Download qna attach file, no=" + no, loginAdmin.getId());
        } catch (Exception e) {

            log.error("Fail to download attach files, msg = " + e.getMessage());
        }

        return fileByte;
    }
}