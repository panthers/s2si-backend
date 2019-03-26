package com.cp.s2si.persistence;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.cp.s2si.persistence.models.ProcessorMainModel;

public interface ProcessorRepository extends CrudRepository<ProcessorMainModel, UUID> {

}
