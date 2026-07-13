package com.keyloop.documentviewer.audit

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface DocumentSearchAuditRepository : CrudRepository<DocumentSearchAudit, UUID>
