/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


public class Time {
    public static String passed (String d1, String d2) {

        String result = "";

        LocalDate date1 = null;
        LocalDate date2 = null;

        LocalDateTime dateTime1 = null;
        LocalDateTime dateTime2 = null;

        ZoneId polandZone = ZoneId.of("Europe/Warsaw");
        ZonedDateTime zonedDateTime1 = null;
        ZonedDateTime zonedDateTime2 = null;

        LocalTime time1 = null;
        LocalTime time2 = null;

        // initialize date1 and date2
        try {
            if (d1.contains("T") && d2.contains("T")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                dateTime1 = LocalDateTime.parse(d1, formatter);
                dateTime2 = LocalDateTime.parse(d2, formatter);

                zonedDateTime1 = dateTime1.atZone(polandZone);
                zonedDateTime2 = dateTime2.atZone(polandZone);

                date1 = dateTime1.toLocalDate();
                date2 = dateTime2.toLocalDate();

                time1 = dateTime1.toLocalTime();
                time2 = dateTime2.toLocalTime();
            }
            else {
                date1 = LocalDate.parse(d1);
                date2 = LocalDate.parse(d2);
            }
        } catch (Exception e) {
            return "*** " + e;
        }

        // ========================= printing =========================

        // day & month
        int day1 = date1.getDayOfMonth();
        int day2 = date2.getDayOfMonth();
        int month1 = date1.getMonthValue();
        int month2 = date2.getMonthValue();
        int year1 = date1.getYear();
        int year2 = date2.getYear();

        // Strings: month and week day names
        Locale locale = new Locale("pl", "PL");
        DateFormatSymbols dfs = new DateFormatSymbols(locale);

        // get month name
        String month1Name = dfs.getMonths()[month1 - 1];
        String month2Name = dfs.getMonths()[month2 - 1];

        // get week day name
        DayOfWeek dayOfWeek1 = date1.getDayOfWeek();
        DayOfWeek dayOfWeek2 = date2.getDayOfWeek();

        String day1Name = dayOfWeek1.getDisplayName(TextStyle.FULL, locale);
        String day2Name = dayOfWeek2.getDisplayName(TextStyle.FULL, locale);

        if (time1 != null && time2 != null) {
            result += "Od " + day1 + " " + month1Name + " " + year1 + " (" + day1Name + ") godz. " + time1 + " do " + day2 + " " + month2Name + " " + year2 + " (" + day2Name + ") godz. " + time2;
        } else {
            result += "Od " + day1 + " " + month1Name + " " + year1 + " (" + day1Name +  ") do " + day2 + " " + month2Name + " " + year2 + " (" + day2Name + ")";
        }




        // between dates
        Period period = Period.between(date1, date2);
        // sum of days in period
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
//        System.out.println(daysBetween);

//        int sumdays = period.getYears()*365 + period.getMonths()*30 + period.getDays();
//        System.out.println(sumdays);

        // dni, tygodni
        if (daysBetween == 1) {
            result += ("\n - mija: " + daysBetween + " dzień, tygodni ");
        } else {
            result += ("\n - mija: " + daysBetween + " dni, tygodni ");
        }
        double weeks = (double) daysBetween/7;
//        System.out.println(weeks);

        String weeksString = String.format("%.2f", weeks);
//        System.out.println(weeksString);
//        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//        weeks = Double.parseDouble(decimalFormat.format(weeks));

        if (weeks == 0) {
            result += ("0");
        } else {
            result += weeksString;
        }

        // godzin, minut
        // only if time is given
        if (time1 != null && time2 != null) {
            Duration duration = Duration.between(zonedDateTime1, zonedDateTime2);
            long hours = duration.toHours();
            long minutes = duration.toMinutes();

            result += ("\n - godzin: " + hours + ", minut: " + minutes);
        }

        // kalendarzowo
        if (daysBetween > 0) {
            result += ("\n - kalendarzowo: ");

            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            if (years > 0) {
                switch (years) {
                    case 1:
                        result += (years + " rok");
                        break;
                    case 2: case 3: case 4:
                        result += (years + " lata");
                        break;
                    default:
                        result += (years + " lat");
                        break;
                }
            }

            if (months > 0) {
                if (years > 0) {
                    result += (", ");
                }
                switch (months) {
                    case 1:
                        result += (months + " miesiąc");
                        break;
                    case 2: case 3: case 4:
                        result += (months + " miesiące");
                        break;
                    default:
                        result += (months + " miesięcy");
                        break;
                }
            }

            if (days > 0) {
                if (years > 0 || months > 0) {
                    result += (", ");
                }
                if (days == 1) {
                    result += (days + " dzień");
                } else {
                    result += (days + " dni");
                }
            }

        }

        
        return result;
    }
}
