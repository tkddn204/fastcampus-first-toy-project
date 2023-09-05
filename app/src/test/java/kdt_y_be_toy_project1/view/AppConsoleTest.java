package kdt_y_be_toy_project1.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class AppConsoleTest {

    @DisplayName("시작 프로세스에서는")
    @Nested
    class Context_StartProcess {

        private static final String SELECT_MENU_DISPLAY = """
            ==========================================================
                   #  여행 여정을 기록과 관리하는 SNS 서비스  #

                1. 여행기록
                2. 여정기록
                3. 여행조회
                4. 여정조회
                5. 종료
            ==========================================================
            시작할 메뉴번호를 입력 하세요:\t""";

        @DisplayName("콘솔의 상태가 종료상태가 아니다")
        @Test
        void console_shutdownStatus_isFalse() {
            // given
            AppConsole appConsole = new AppConsole();

            // when
            boolean isComplete = appConsole.isShutdown();

            // then
            assertThat(isComplete).isFalse();
        }

        @DisplayName("콘솔 시작 시 시작 메시지를 출력한다.")
        @Test
        void startMessage_willPrinted() {
            // given
            AppConsole appConsole = new AppConsole();

            // when
            String message = appConsole.flush();

            // then
            assertThat(message).isEqualTo(SELECT_MENU_DISPLAY);
        }
    }

    @DisplayName("메뉴 선택 프로세스에서는")
    @Nested
    class Context_MenuSelectProcess {

        private static final String SELECT_MENU_DISPLAY = """
            ==========================================================
                   #  여행 여정을 기록과 관리하는 SNS 서비스  #

                1. 여행기록
                2. 여정기록
                3. 여행조회
                4. 여정조회
                5. 종료
            ==========================================================
            시작할 메뉴번호를 입력 하세요:\t""";

        @DisplayName("1~5 제외를 입력하면 다시 메뉴 선택을 제안한다.")
        @ValueSource(strings = {"0", "6", "문자열", "1903812390-12-90312", "ALSKJA", "*@#!", "😀"})
        @ParameterizedTest
        void inputNotInclude_range1To5_willMenuSelectAgain(String input) {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.flush();

            // when
            appConsole.processInput(input);
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).contains(SELECT_MENU_DISPLAY);
        }


        @DisplayName("1번을 선택하면 여행 기록 프로세스로 이동한다.")
        @Test
        void input1_theTripSaveProcessor_willRun() {
            // given
            String saveTripProcessDisplay = """
                ==========================================================
                                #    여행기록 메뉴    #
                ==========================================================                           
                """;

            AppConsole appConsole = new AppConsole();
            appConsole.flush();

            // when
            appConsole.processInput("1");
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).contains(saveTripProcessDisplay);
        }
    }

    @DisplayName("여행 입력 프로세스에서는")
    @Nested
    class Context_InsertTripProcess {

        @DisplayName("가장 먼저 시작일을 입력 요청메시지를 출력한다.")
        @Test
        void requestToInsertStartDate_message_willDisplay() {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.flush();
            appConsole.processInput("1");

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).endsWith("시작일(yyyy-mm-dd)을 입력하세요:\t");
        }
    }

    @DisplayName("여행 시작일 입력 프로세스에서는")
    @Nested
    class Context_InsertTripStartDate {

        @DisplayName("yyyy-mm-dd 포맷으로 입력하지 않으면 재입력을 요청한다.")
        @ValueSource(strings = {"20001011", "2000/10/11", "2000년10월11일", "2000_10_1청1"})
        @ParameterizedTest
        void notMatchThisFormat_willRequestAgain(String badFormat) {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.flush();
            appConsole.processInput("1");

            // when
            appConsole.flush();
            appConsole.processInput(badFormat);
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).startsWith("입력 포맷(yyyy-mm-dd)을 지켜주세요. (예: 2023-09-05)")
                .endsWith("시작일(yyyy-mm-dd)을 입력하세요:\t");
        }

        @DisplayName("시작일을 정확하게 입력하면 종료일 입력 요청메시지를 출력한다.")
        @Test
        void insertStartDateCorrectly_insertEndDateRequestMessage_willDisplay() {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.flush();
            appConsole.processInput("1");
            appConsole.flush();
            appConsole.processInput("2023-09-05");

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).isEqualTo("종료일(yyyy-mm-dd)을 입력하세요:\t");
        }
    }

    @DisplayName("여행 종료일 입력 프로세스에서는")
    @Nested
    class Context_InsertTripEndDate {

        @DisplayName("yyyy-mm-dd 포맷으로 입력하지 않으면 재입력을 요청한다.")
        @ValueSource(strings = {"20001011", "2000/10/11", "2000년10월11일", "2000_10_11"})
        @ParameterizedTest
        void notMatchThisFormat_willRequestAgain(String badFormat) {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.processInput("1");
            appConsole.processInput("2023-09-05");
            appConsole.flush();
            appConsole.processInput(badFormat);

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).startsWith("입력 포맷(yyyy-mm-dd)을 지켜주세요. (예: 2023-09-05)")
                .endsWith("종료일(yyyy-mm-dd)을 입력하세요:\t");
        }

        @DisplayName("종료일이 시작일보다 빠르면 재입력을 요청한다.")
        @CsvSource(value = {"2023-09-05, 2023-09-04", "2023-09-01, 2023-08-30", "2023-09-01, 2022-09-01"})
        @ParameterizedTest
        void endDate_isEarlierThanStartDate_willRequestAgain(String startDate, String endDate) {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.processInput("1");
            appConsole.processInput(startDate);
            appConsole.flush();
            appConsole.processInput(endDate);

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).startsWith("종료일은 시작일보다 빠를 수 없습니다.")
                .endsWith("종료일(yyyy-mm-dd)을 입력하세요:\t");
        }


        @DisplayName("종료일을 성공적으로 입력하면 여행명 입력 요청메시지를 출력한다.")
        @Test
        void insertEndDateCorrectly_insertTripNameRequestMessage_willDisplay() {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.processInput("1");
            appConsole.processInput("2023-09-01");
            appConsole.flush();
            appConsole.processInput("2023-09-02");

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).endsWith("여행명을 입력하세요:\t");
        }
    }

    @DisplayName("여행명 입력 프로세스에서는")
    @Nested
    class Context_InsertTripName {

        @DisplayName("여행명을 성공적으로 입력하면 최종 여행 정보를 출력한다.")
        @Test
        void insertTripNameCorrectly_finalTripInfo_willDisplay() {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.processInput("1");
            String startDate = "2023-09-01";
            appConsole.processInput(startDate);
            String endDate = "2023-09-02";
            appConsole.processInput(endDate);
            appConsole.flush();
            String tripName = "신나는 1박2일";
            appConsole.processInput(tripName);

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).contains(startDate, endDate, tripName);
        }

        @DisplayName("여행명을 성공적으로 입력하면 저장 확정 여부를 출력한다.")
        @Test
        void insertEndDateCorrectly_insertTripNameRequestMessage_willDisplay() {
            // given
            AppConsole appConsole = new AppConsole();
            appConsole.processInput("1");
            appConsole.processInput("2023-09-01");
            appConsole.processInput("2023-09-02");
            appConsole.flush();
            appConsole.processInput("신나는 1박2일");

            // when
            String displayMessage = appConsole.flush();

            // then
            assertThat(displayMessage).endsWith("저장하시겠습니까?(Y/N):\t");
        }
    }
}