package de.beosign.jpatest.service;

import java.sql.SQLException;

import javax.ejb.Local;

import de.beosign.jpatest.TestResult;

@Local
public interface TestService
{

    TestResult testTxReqTxReq() throws SQLException;

    TestResult testTxReqTxNew() throws SQLException;

    TestResult testTxReqTxReqInnerException();

    TestResult testTxReqTxReqNewInnerException() throws SQLException;

    TestResult testTxReqTxReqOuterException();

}
