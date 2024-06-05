package DB2024Team03;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReviewController {
    public List<ReviewDTO> getMemberReviews(int memberId) {
        List<ReviewDTO> reviews = new ArrayList<>();

        String query = "SELECT R.review_id, R.content, R.date, M.name AS product_name " +
                " FROM DB2024_Review R INNER JOIN DB2024_Mealkit M ON R.mealkit_id = M.mealkit_id " +
                "WHERE R.member_id = ?";

        try (Connection conn = DBconnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reviews.add(new ReviewDTO(rs.getInt("review_id"), rs.getString("product_name"), rs.getString("content"), rs.getDate("date")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public void showMemberReviews(int memberId) {
        List<ReviewDTO> reviews = getMemberReviews(memberId);
        if (reviews.isEmpty()) {
            System.out.println("작성된 리뷰가 없습니다.");
        } else {
            for (ReviewDTO review : reviews) {
                System.out.println("\n[상품이름]" + review.getProductName());
                System.out.println("[리뷰내용]" + review.getContent());
                System.out.println("[작성날짜]" + review.getDate().toString());
            }
            System.out.println("=====================");
        }
    }


    public void showReview(int mealkitId) {
        String query = "SELECT name, price, stock FROM DB2024_Mealkit WHERE mealkit_id = ?";
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, mealkitId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("[상품 이름] " + rs.getString("name"));
                System.out.println("[가격] " + rs.getInt("price"));
                System.out.println("[재고] " + rs.getInt("stock"));
            } else {
                System.out.println("해당 상품이 존재하지 않습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createReview(int memberId, int mealkitId, String content, Scanner sc) {
        String query = "INSERT INTO DB2024_Review (content, date, mealkit_id, member_id) VALUES (?, CURRENT_DATE(), ?, ?)";
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, content);
            pstmt.setInt(2, mealkitId);
            pstmt.setInt(3, memberId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("리뷰가 성공적으로 등록되었습니다.");
                return;
            } else {
                System.out.println("리뷰 등록에 실패했습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Prints the stack trace for debugging
        }
    }

    public void deleteReview(int id, int MealkitId) {
        String deleteReview = "DELETE FROM DB2024_Review WHERE member_id=? AND mealkit_id=?";

        try (// DB 연결을 위한 정보를 설정
             Connection conn = DBconnect.getConnection();
             PreparedStatement statement = conn.prepareStatement(deleteReview);
        ){

            statement.setInt(1, id);
            statement.setInt(2, MealkitId);

            try {
                statement.executeUpdate();

                System.out.print("리뷰가 제거되었습니다.\n");
                return;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
