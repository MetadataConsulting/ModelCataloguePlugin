package org.modelcatalogue.core.util

import org.modelcatalogue.core.CatalogueElement
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter

class CatalogueElementFinder {

    static final Set<String> catalogueElementClasses

    private static final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return true
        }
    }

    static {
        provider.addIncludeFilter(new TypeFilter() {
            boolean isCatalogueElementOrSubclass(ClassMetadata classMetadata, MetadataReaderFactory metadataReaderFactory) {
                if (classMetadata.className == CatalogueElement.name) {
                    return true
                }
                if (!classMetadata.hasSuperClass()) {
                    return false
                }
                if (classMetadata.superClassName == CatalogueElement.name) {
                    return true
                }
                return isCatalogueElementOrSubclass(metadataReaderFactory.getMetadataReader(classMetadata.superClassName).classMetadata, metadataReaderFactory)

            }

            @Override
            boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                try {
                    return isCatalogueElementOrSubclass(metadataReader.classMetadata, metadataReaderFactory)
                } catch (FileNotFoundException ignored) {
                    return false
                }
            }
        })

        Set<BeanDefinition> candidates = provider.findCandidateComponents("")
        catalogueElementClasses = Collections.unmodifiableSet(candidates.collect { it.beanClassName } as Set)
    }


}
