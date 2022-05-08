package com.onetop.webadmin.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onetop.webadmin.models.authority.AdminMember;

//import com.onetop.webadmin.models.authority.AdminMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 권한별 페이지 접속권한 체크 interceptor
 */
@Component
public class AccessPermissionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(AccessPermissionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getSession().getAttribute("member") == null) {
            response.sendRedirect("/login");
            return false;
        }
//        String requestURI = request.getRequestURI();
//        AdminMember adminMember = (AdminMember) request.getSession().getAttribute("member");
//
//        // 어드민 O // 수당지급내역 작업 안됨
//        if (adminMember.getRole() == 1) {
//            if (requestURI.indexOf("/auth") != -1) {
//            //if (requestURI.equals("/sys6") || requestURI.equals("/mem1") || requestURI.equals("/mem8") || requestURI.equals("/mem4") || requestURI.equals("/mem14") || requestURI.indexOf("/board") != -1 || requestURI.indexOf("/auth") != -1) {
////                response.sendRedirect("/mon1");
//                return false;
//            }
//        }
//        // 어드민 S //
//        else if (adminMember.getRole() == 2) {
////            if (requestURI.indexOf("/auth") != -1 || requestURI.equals("/sys2") ) {
////                response.sendRedirect("/mon1");
//                return false;
//            }
//        }
//        // 어드민 M
//        else if (adminMember.getRole() == 3 ) {
////            if (requestURI.indexOf("/mon") != -1 || requestURI.equals("/mem4") || requestURI.indexOf("/auth") != -1) {
////                response.sendRedirect("/sys1");
//                return false;
//            }
//        }

        return true;
    }


    @Override
    public void postHandle (HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        AdminMember adminMember = (AdminMember) request.getSession().getAttribute("member");
        String security_proposal = adminMember.getSecurity_proposal();

        String requestURI = request.getRequestURI();

        String YN = "N";

        if(requestURI.indexOf("/dashboard") != -1) {
            if (security_proposal.indexOf("ZA001") != -1 || security_proposal.indexOf("ZA111") != -1) {
                YN = "Y";
            }
        } else if(requestURI.indexOf("/basicextrapay") != -1) {
            if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB301")!=-1 || security_proposal.indexOf("ZB311")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/sponsorextrapay") != -1) {
            if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB301")!=-1 || security_proposal.indexOf("ZB321")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/encourageextrapay") != -1) {
            if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB301")!=-1 || security_proposal.indexOf("ZB331")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/rankextrapay") != -1) {
            if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB301")!=-1 || security_proposal.indexOf("ZB341")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/member_detail") != -1) {
            if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC111")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/bank1") != -1) {
            if(security_proposal.indexOf("ZD001")!=-1 || security_proposal.indexOf("ZD101")!=-1 || security_proposal.indexOf("ZD111")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/bank2") != -1) {
            if(security_proposal.indexOf("ZD001")!=-1 || security_proposal.indexOf("ZD201")!=-1 || security_proposal.indexOf("ZD211")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/bank31") != -1 ) {
            if(security_proposal.indexOf("ZD001")!=-1 || security_proposal.indexOf("ZD201")!=-1 || security_proposal.indexOf("ZD311")!=-1 || security_proposal.indexOf("ZD321")!=-1) {
                YN = "Y";
            }
            
        }else if(requestURI.indexOf("/bank33") != -1 ) {
        	// @todo: 관리자 출금 요청 권한 검토 필요 (기획서에 정의되지 않아 출금요청과 동일하게 처리중)
            if(security_proposal.indexOf("ZD001")!=-1 || security_proposal.indexOf("ZD201")!=-1 || security_proposal.indexOf("ZD311")!=-1 || security_proposal.indexOf("ZD321")!=-1) {
                YN = "Y";
            }
            
        }else if(requestURI.indexOf("/bank41") != -1) {
            if(security_proposal.indexOf("ZD001")!=-1 || security_proposal.indexOf("ZD201")!=-1 || security_proposal.indexOf("ZD321")!=-1) {
                YN = "Y";
            }

        }else if(requestURI.indexOf("/board1") != -1) {
            if(security_proposal.indexOf("ZE001")!=-1 || security_proposal.indexOf("ZE111")!=-1) {
                YN = "Y";
            }
        }else if(requestURI.indexOf("/sys") != -1) {
            if(requestURI.indexOf("/sys1") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB101")!=-1 || security_proposal.indexOf("ZB111")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/sys2") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB101")!=-1 || security_proposal.indexOf("ZB121")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/sys3") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB201")!=-1 || security_proposal.indexOf("ZB211")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/sys6") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB511")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/sys7") != -1) {
                if(security_proposal.indexOf("ZE001")!=-1 || security_proposal.indexOf("ZE211")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/sys9") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB201")!=-1 || security_proposal.indexOf("ZB221")!=-1) {
                    YN = "Y";
                }
            }

        }else if(requestURI.indexOf("/mem") != -1) {
            if(requestURI.indexOf("/mem1") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC201")!=-1 || security_proposal.indexOf("ZC211")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem3") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC301")!=-1 || security_proposal.indexOf("ZC321")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem4") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC301")!=-1 || security_proposal.indexOf("ZC311")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem5") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC401")!=-1 || security_proposal.indexOf("ZC411")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem6") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC401")!=-1 || security_proposal.indexOf("ZC421")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem8") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC201")!=-1 || security_proposal.indexOf("ZC221")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem17") != -1) {
                if(security_proposal.indexOf("ZC001")!=-1 || security_proposal.indexOf("ZC201")!=-1 || security_proposal.indexOf("ZC231")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/mem18") != -1) {
                if(security_proposal.indexOf("ZB001")!=-1 || security_proposal.indexOf("ZB411")!=-1) {
                    YN = "Y";
                }

            }

        }else if(requestURI.indexOf("/auth") != -1) {
            if(requestURI.indexOf("/auth1") != -1) {

                if(security_proposal.indexOf("ZF001")!=-1 || security_proposal.indexOf("ZF111")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/auth3") != -1) {
                if(security_proposal.indexOf("ZF001")!=-1 || security_proposal.indexOf("ZF211")!=-1) {
                    YN = "Y";
                }

            }else if(requestURI.indexOf("/auth2") != -1) {
                if(security_proposal.indexOf("ZF001")!=-1 || security_proposal.indexOf("ZF311")!=-1) {
                    YN = "Y";
                }

            }

        }else if(requestURI.indexOf("/admin_mod") != -1) {
            if(security_proposal.indexOf("ZF001")!=-1 || security_proposal.indexOf("ZF111")!=-1) {
                YN = "Y";
            }
        }

        request.setAttribute("securityYN", YN);

    }

}
