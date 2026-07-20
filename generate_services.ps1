$entities = @("User", "Role", "Address", "Category", "Brand", "Product", "ProductImage", "Inventory", "Cart", "Wishlist", "Order", "Payment", "Review", "Coupon", "Notification")

New-Item -ItemType Directory -Force -Path "src/main/java/com/jk1/service/impl"

foreach ($entity in $entities) {
    $service = "${entity}Service"
    $impl = "${entity}ServiceImpl"
    $repo = "${entity}Repository"
    $varName = $entity.Substring(0,1).ToLower() + $entity.Substring(1)
    
    $interfaceContent = @"
package com.jk1.service;

import com.jk1.entity.$entity;
import java.util.List;
import java.util.Optional;

public interface $service {
    $entity save($entity $varName);
    Optional<$entity> findById(Long id);
    List<$entity> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
"@
    Set-Content -Path "src/main/java/com/jk1/service/${service}.java" -Value $interfaceContent
    
    $implContent = @"
package com.jk1.service.impl;

import com.jk1.entity.$entity;
import com.jk1.repository.$repo;
import com.jk1.service.$service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class $impl implements $service {

    private final $repo ${varName}Repository;

    @Override
    public $entity save($entity $varName) {
        return ${varName}Repository.save($varName);
    }

    @Override
    public Optional<$entity> findById(Long id) {
        return ${varName}Repository.findById(id);
    }

    @Override
    public List<$entity> findAll() {
        return ${varName}Repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        ${varName}Repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ${varName}Repository.existsById(id);
    }
}
"@
    Set-Content -Path "src/main/java/com/jk1/service/impl/${impl}.java" -Value $implContent
}

Write-Host "Services successfully generated!"
