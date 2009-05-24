from django.db import models
from django.contrib.auth.models import User
from django.contrib.contenttypes.models import ContentType


class ShareableItem(models.Model):
    content_type = models.ForeignKey(ContentType, editable=False, null=True)
    owner = models.ForeignKey(User, related_name='my_shareable_items')

    def save(self):
        if(not self.content_type):
            self.content_type = \
                ContentType.objects.get_for_model(self.__class__)
        self.save_base()

    def as_leaf_class(self):
        content_type = self.content_type
        model = content_type.model_class()
        if(model == ShareableItem):
            return self
        return model.objects.get(id=self.id)


class ItemSharedWithUser(models.Model):
    item = models.ForeignKey(ShareableItem, related_name='users')
    user = models.ForeignKey(User, related_name='others_shared_items')
