package com.ms3_inc.apigee.basicauth;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;

import java.sql.*;

public class Authenticate implements Execution {
    @Override
    public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {

        String user = messageContext.getVariable("auth.username");
        String pass = messageContext.getVariable("auth.password");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://database-1.cxkrs8lwydn3.us-east-1.rds.amazonaws.com:3306/basicauth",
                "admin", "apigee123")) {
            PreparedStatement statement = conn.prepareStatement("select count(*) as count from users where user=? and pass=?");
            statement.setString(1, user);
            statement.setString(2, pass);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                messageContext.setVariable("is_auth", resultSet.getInt("count") > 0);
            }

            return ExecutionResult.SUCCESS;
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

            ExecutionResult result = ExecutionResult.ABORT;
            result.setErrorResponse(ex.getMessage());

            return result;
        }

    }
}
