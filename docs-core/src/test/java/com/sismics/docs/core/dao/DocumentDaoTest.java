package com.sismics.docs.core.dao;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.util.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Test the Document DAO.
 * 
 * @author jm-dev
 */
public class DocumentDaoTest extends BaseTransactionalTest {
    
    @Test
    public void testCreateDocument() throws Exception {
        // Create a test user
        String userId = createUser("testuserbasic").getId();
        
        // Create a document
        DocumentDao documentDao = new DocumentDao();
        Document document = new Document();
        document.setUserId(userId);
        document.setTitle("My document");
        document.setDescription("A simple test document");
        document.setLanguage("eng");
        document.setCreateDate(new Date());
        
        // Save the document
        String documentId = documentDao.create(document, userId);
        TransactionUtil.commit();
        
        // Get the document
        Document retrievedDoc = documentDao.getById(documentId);
        
        // Verify the document
        Assert.assertNotNull("Document should not be null", retrievedDoc);
        Assert.assertEquals("Title should match", "My document", retrievedDoc.getTitle());
        Assert.assertEquals("Description should match", "A simple test document", retrievedDoc.getDescription());
        Assert.assertEquals("Language should match", "eng", retrievedDoc.getLanguage());
        Assert.assertEquals("User ID should match", userId, retrievedDoc.getUserId());
    }
} 